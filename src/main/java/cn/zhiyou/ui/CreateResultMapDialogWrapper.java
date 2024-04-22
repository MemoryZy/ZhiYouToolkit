package cn.zhiyou.ui;

import cn.hutool.core.util.StrUtil;
import cn.zhiyou.constant.ZhiYouConstant;
import cn.zhiyou.entity.template.ColumnEntity;
import cn.zhiyou.entity.template.ResultMapEntity;
import cn.zhiyou.ui.basic.MultiRowTextField;
import cn.zhiyou.ui.basic.TextFieldErrorPopupDecorator;
import cn.zhiyou.utils.*;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.sql.SqlFileType;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.awt.RelativePoint;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wcp
 * @since 2023/12/11
 */
public class CreateResultMapDialogWrapper extends DialogWrapper {
    private JPanel rootPanel;
    private EditorTextField sqlEditorTextField;
    private final Project project;
    private final boolean needCopy;
    private final Editor editor;
    private final Document document;
    private final PsiClass psiClass;
    private final TextFieldErrorPopupDecorator errorPopupDecorator;
    private final AnActionEvent event;

    public CreateResultMapDialogWrapper(@NotNull AnActionEvent event, @Nullable Project project, Editor editor, Document document,
                                        PsiClass psiClass, boolean needCopy) {
        super(project, true);
        this.project = project;
        this.psiClass = psiClass;
        this.needCopy = needCopy;
        this.editor = editor;
        this.document = document;
        this.event = event;

        // 非模态弹窗（可以在展示弹窗时，不限制主页面的操作）
        setModal(false);

        errorPopupDecorator = new TextFieldErrorPopupDecorator(getRootPane(), sqlEditorTextField);

        getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                sqlEditorTextField.requestFocusInWindow();
            }
        });

        setOKButtonText("生成");
        setCancelButtonText("取消");

        init();
        setTitle("根据实体类生成对应ResultMap");
    }

    private void createUIComponents() {
        sqlEditorTextField = new MultiRowTextField("", project, SqlFileType.INSTANCE);
        sqlEditorTextField.setFont(new Font("Consolas", Font.PLAIN, 15));
        sqlEditorTextField.setPreferredSize(new Dimension(480, 450));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            if (executeOkAction()) {
                applyFields();
                close(OK_EXIT_CODE);
            }
        }
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        // 该验证方法在点击ok按钮事件前执行，是点击了ok按钮后才执行
        return super.doValidate();
    }


    /**
     * 执行Ok按钮点击操作
     */
    private boolean executeOkAction() {
        String sqlText = StrUtil.trimToNull(sqlEditorTextField.getText());
        if (StrUtil.isBlank(sqlText)) {
            errorPopupDecorator.setError("SQL为空");
            // tip("SQL不能为空", NotificationType.WARNING);
            return false;
        }

        // 解析sql（左：列名，右别名）
        List<ImmutablePair<String, String>> immutablePairs = new ArrayList<>();

        try {
            Statement statement = CCJSqlParserUtil.parse(sqlText);
            if (statement instanceof PlainSelect plainSelect) {
                List<SelectItem<?>> selectItems = plainSelect.getSelectItems();

                for (SelectItem<?> selectItem : selectItems) {
                    Expression expression = selectItem.getExpression();
                    // 普通列
                    if (expression instanceof Column column) {
                        String columnName = column.getColumnName();
                        Alias alias = selectItem.getAlias();
                        String aliasName = (Objects.isNull(alias)) ? "" : alias.getName();
                        // 左：列名，右：别名
                        immutablePairs.add(ImmutablePair.of(columnName, aliasName));
                        // } else if (expression instanceof Function) {
                    } else {
                        // 不只有/Function，可能还有其他的，所以都取别名
                        Alias alias = selectItem.getAlias();
                        if (Objects.nonNull(alias)) {
                            // 左：列名，右：别名
                            immutablePairs.add(ImmutablePair.of("", alias.getName()));
                        }
                    }
                }
            }
        } catch (JSQLParserException e) {
            errorPopupDecorator.setError("SQL解析失败");
            // tip("SQL解析失败", NotificationType.WARNING);
            return false;
        }

        if (immutablePairs.isEmpty()) {
            errorPopupDecorator.setError("SQL缺少列");
            // tip("SQL中没有列", NotificationType.WARNING);
            return false;
        }

        String className = psiClass.getName();
        // 列信息
        List<Map<String, Object>> columnMaps = new ArrayList<>();

        // 记录未匹配成功的列与字段
        List<String> unMatchPropertyList = new ArrayList<>();

        // 属性数
        int propertyNum = 0;

        PsiField[] fields = ActionUtil.getAllFieldFilterStatic(psiClass);
        // 映射字段与sql列
        for (PsiField field : fields) {
            // 类属性名
            String propertyName = field.getName();
            // Sql列名
            String fieldName = getColumnName(immutablePairs, propertyName);
            // 根据JavaType获取JdbcType
            String jdbcType = CodeCreateUtil.getJdbcType(field.getType().getCanonicalText());

            propertyNum++;
            // 对象类型就跳过
            if (StringUtils.isBlank(jdbcType)) {
                propertyNum--;
                continue;
            }

            // 没匹配到
            if (StringUtils.isBlank(fieldName)) {
                unMatchPropertyList.add(propertyName);
                continue;
            }

            Map<String, Object> columnMap = new ColumnEntity()
                    .setColumnName(fieldName)
                    .setJdbcType(jdbcType)
                    // 处理<ResultMap>中的属性名与类属性的映射
                    .setPropertyName(handlePropertyMapping(propertyName))
                    .toMap();

            columnMaps.add(columnMap);
        }

        if (columnMaps.isEmpty()) {
            errorPopupDecorator.setError(StrUtil.format("Sql与{}类之间没有匹配的列", className));
            // tip(StrUtil.format("SQL与{}间没有匹配的列", className), NotificationType.WARNING);
            return false;
        }

        Map<String, Object> resultMap = new ResultMapEntity()
                .setName(className)
                .setQualifiedName(psiClass.getQualifiedName())
                .setColumns(columnMaps)
                .toMap();

        // 根据模板生成
        String resultMapText = ActionUtil.createTextWithTemplate(project, "ResultMap", resultMap);

        if (StrUtil.isBlank(resultMapText)) {
            Component component = event.getInputEvent().getComponent();
            RelativePoint relativePoint = PopupUtil.calculateAbovePoint(component);
            PopupUtil.showHTmlTextBalloon("ResultMap生成失败...", MessageType.ERROR, relativePoint, Balloon.Position.above);

            // ActionUtil.showGotItTip(
            //         "zhiYou.mybatis.resultMap.listener.tip.id",
            //         "Failed",
            //         "ResultMap生成失败...",
            //         Messages.getErrorIcon(),
            //         3000,
            //         sqlEditorTextField,
            //         GotItTooltip.BOTTOM_MIDDLE);
            // errorPopupDecorator.setWindowError("ResultMap生成失败...");
            return false;
        }

        if (needCopy) {
            // 拷贝到剪贴板
            ActionUtil.setClipboard(resultMapText);

            String tip;
            if (unMatchPropertyList.size() <= 3) {
                tip = unMatchPropertyList.isEmpty()
                        ? ""
                        : "<br/><i>未匹配属性:</i> &nbsp; &nbsp;[ " + StrUtil.join(" , ", unMatchPropertyList) + " ]";
            } else {
                tip = "<br/> --- <i>未匹配属性:</i><br/> - " + StrUtil.join("<br/> - ", unMatchPropertyList);
            }

            String content = StrUtil.format(ZhiYouConstant.mapperNotify, immutablePairs.size(), propertyNum, unMatchPropertyList.size(), tip);
            NotificationUtil.notifyWithLog("已复制到剪贴板", content, NotificationType.INFORMATION, project);
        } else {
            // --> 获取光标所在的当前行
            int currentLine = ActionUtil.getCurrentLine(editor, document);
            // 当前行开始结束的偏移量
            ImmutablePair<Integer, Integer> offset = ActionUtil.getCurrentLineOffset(currentLine, document);
            // 替换到原来光标所在的位置
            ActionUtil.runWriteCommandAction(project, () -> document.replaceString(offset.getLeft(), offset.getRight(), resultMapText));

            String tip;
            if (unMatchPropertyList.size() <= 3) {
                tip = unMatchPropertyList.isEmpty()
                        ? ""
                        : "<br/><i>未匹配属性:</i> &nbsp; &nbsp;[ " + StrUtil.join(" , ", unMatchPropertyList) + " ]";
            } else {
                tip = "<br/> --- <i>未匹配属性:</i><br/> - " + StrUtil.join("<br/> - ", unMatchPropertyList);
            }

            String content = StrUtil.format(ZhiYouConstant.mapperNotify, immutablePairs.size(), propertyNum, unMatchPropertyList.size(), tip);
            NotificationUtil.notifyWithLog("成功", content, NotificationType.INFORMATION, project);
        }

        return true;
    }


    /**
     * 处理属性名映射
     *
     * @param propertyName 属性名
     * @return 映射后的属性名
     */
    public static String handlePropertyMapping(String propertyName) {
        char[] chars = propertyName.toCharArray();
        // 首字母大写并且第二个字母小写或第二个字符为下划线
        if (Character.isUpperCase(chars[0]) && (Character.isLowerCase(chars[1]) || '_' == chars[1])) {
            chars[0] = Character.toLowerCase(chars[0]);
            propertyName = String.valueOf(chars);
        }

        return propertyName;
    }


    /**
     * 根据属性名称获取列名，有别名就优先别名
     *
     * @param immutablePairs 不可变对列表，用于存储列名、别名
     * @param propertyName   属性名称
     * @return 列名，如果找不到对应的列名则返回null
     */
    private String getColumnName(List<ImmutablePair<String, String>> immutablePairs, String propertyName) {
        for (ImmutablePair<String, String> immutablePair : immutablePairs) {
            String columnName = immutablePair.getLeft();
            String aliasName = immutablePair.getRight();

            // 如果有别名，则优先匹配别名
            if (StrUtil.isNotBlank(aliasName) && CommonUtil.matchCase(aliasName, propertyName)) {
                return aliasName;
            }

            // 别名匹配不到或者别名为空，那么就匹配列名
            if (CommonUtil.matchCase(columnName, propertyName)) {
                return columnName;
            }
        }

        return null;
    }

}
