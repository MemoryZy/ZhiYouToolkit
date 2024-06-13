package cn.zhiyou.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhiyou.action.JavaBeanConvertToJsonAction;
import cn.zhiyou.enums.PopupTypeEnum;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.designer.clipboard.SimpleTransferable;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.lang.FileASTNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.impl.HTMLEditorProvider;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.BalloonBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.*;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Function;

/**
 * 行为工具
 *
 * @author Memory
 * @since 2023/11/27
 */
public class ActionUtil {

    private static final Logger LOG = Logger.getInstance(JavaBeanConvertToJsonAction.class);


    // 为了避免出现 anchorBefore == null || anchorBefore.getTreeParent() == parent 错误
    // 可以用 newClass.addBefore(dataAnnotation, newClass.getFirstChild()); 去处理

    /**
     * 用两种方法获取PsiClass
     *
     * @param event 事件源
     * @return Class
     */
    public static PsiClass getPsiClass(AnActionEvent event) {
        PsiClass psiClass = null;
        try {
            psiClass = getCurrentPsiClassByFile(event);
            if (Objects.isNull(psiClass)) {
                psiClass = getCurrentPsiClassByOffset(event);
            }
        } catch (Throwable e) {

        }

        return psiClass;
    }


    /**
     * 根据Java文件获取当前Class文件
     *
     * @param event 事件信息
     * @return Class
     */
    public static PsiClass getCurrentPsiClassByFile(AnActionEvent event) {
        PsiClass psiClass = null;
        PsiFile psiFile = getPsiFile(event);
        if (Objects.nonNull(psiFile)) {
            FileASTNode node = psiFile.getNode();
            PsiElement psi = node.getPsi();
            if (psi instanceof PsiJavaFile psiJavaFile) {
                PsiClass[] classes = psiJavaFile.getClasses();
                psiClass = classes[0];
            }
        }

        return psiClass;
    }


    /**
     * 根据编辑器的偏移量获取当前所在的Class文件（因为利用了编辑器，光标必须在类中，也就是类的上下作用域内）
     *
     * @param event 事件信息
     * @return class
     */
    public static PsiClass getCurrentPsiClassByOffset(AnActionEvent event) {
        PsiFile psiFile = getPsiFile(event);
        Editor editor = getEditor(event);

        if (Objects.nonNull(psiFile) && (psiFile.getFileType() instanceof JavaFileType)) {
            return PsiTreeUtil.getParentOfType(getPsiElementByOffset(editor, psiFile), PsiClass.class);
        }

        return null;
    }


    /**
     * 通过当前光标的偏移量获取当前所在的Psi元素
     *
     * @param event 事件源
     * @return Psi元素
     */
    public static PsiElement getPsiElementByOffset(AnActionEvent event) {
        try {
            Editor editor = getEditor(event);
            PsiFile psiFile = getPsiFile(event);
            return (Objects.isNull(psiFile) || Objects.isNull(editor)) ? null : getPsiElementByOffset(editor, psiFile);
        } catch (Throwable e) {
            return null;
        }
    }


    /**
     * 通过当前光标的偏移量获取当前所在的Psi元素
     * <p>亦可配合 PsiTreeUtil.getParentOfType(element, PsiClass.class)方法来获取该PsiElement所处的区域</p>
     *
     * @param editor  编辑器
     * @param psiFile Psi文件
     * @return Psi元素
     */
    public static PsiElement getPsiElementByOffset(Editor editor, PsiFile psiFile) {
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }


    /**
     * 获取方法中所有的本地变量列表
     *
     * @param psiMethod 方法对象
     * @return 本地变量列表
     */
    public static List<PsiLocalVariable> getPsiLocalVariableInMethod(PsiMethod psiMethod) {
        List<PsiLocalVariable> localVariableList = new ArrayList<>();
        psiMethod.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                super.visitLocalVariable(variable);
                localVariableList.add(variable);
            }
        });

        return localVariableList;
    }


    /**
     * 获取该类的所有字段
     *
     * @param event 事件源
     * @return 所有字段
     */
    public static PsiField[] getAllField(AnActionEvent event) {
        PsiClass psiClass = getPsiClass(event);
        return (Objects.isNull(psiClass)) ? new PsiField[0] : psiClass.getAllFields();
    }


    /**
     * 获取该类的所有字段
     *
     * @param psiClass class
     * @return 所有字段
     */
    public static PsiField[] getAllField(PsiClass psiClass) {
        return (Objects.isNull(psiClass)) ? new PsiField[0] : psiClass.getAllFields();
    }


    /**
     * 获取该类的所有字段
     *
     * @param psiClass class
     * @return 所有字段
     */
    public static PsiField[] getAllFieldFilterStatic(PsiClass psiClass) {
        return (Objects.isNull(psiClass))
                ? new PsiField[0]
                : Arrays.stream(psiClass.getAllFields()).filter(el -> !el.hasModifierProperty(PsiModifier.STATIC)).toArray(PsiField[]::new);
    }


    /**
     * 获取该类的所有非静态且能够写入的字段
     *
     * @param psiClass class
     * @return 所有字段
     */
    public static PsiField[] getAllFieldFilterStaticAndUnWrite(PsiClass psiClass) {
        return (Objects.isNull(psiClass))
                ? new PsiField[0]
                : Arrays.stream(psiClass.getAllFields())
                .filter(el -> !el.hasModifierProperty(PsiModifier.STATIC) && el.isWritable())
                .toArray(PsiField[]::new);
    }


    /**
     * 获取该类所有方法
     *
     * @param psiClass 类
     * @return 方法
     */
    public static PsiMethod[] getAllMethod(PsiClass psiClass) {
        return (Objects.isNull(psiClass)) ? new PsiMethod[0] : psiClass.getAllMethods();
    }


    /**
     * 获取该类所有方法（除静态）
     *
     * @param psiClass 类
     * @return 方法
     */
    public static PsiMethod[] getAllMethodFilterStatic(PsiClass psiClass) {
        return (Objects.isNull(psiClass))
                ? new PsiMethod[0]
                : Arrays.stream(psiClass.getAllMethods()).filter(el -> !el.hasModifierProperty(PsiModifier.STATIC)).toArray(PsiMethod[]::new);
    }


    /**
     * 通过引用类型获取Class
     *
     * @param psiType 引用类型，必须是类引用类型{@link PsiClassReferenceType}
     * @return Class
     */
    public static PsiClass getPsiClassByReferenceType(PsiType psiType) {
        if (psiType instanceof PsiClassReferenceType psiClassReferenceType) {
            return psiClassReferenceType.resolve();
        }

        return null;
    }


    /**
     * 弹窗类选择器
     *
     * @param project 项目
     * @param title   标题
     * @return 选择的类
     */
    public static PsiClass chooseClass(Project project, String title) {
        // 类选择器
        TreeJavaClassChooserDialog treeJavaClassChooserDialog = new TreeJavaClassChooserDialog(title, project);
        // 设置选择框为非模态
        // treeJavaClassChooserDialog.setModal(false);
        // 开启
        treeJavaClassChooserDialog.showDialog();
        // 选择的类
        return treeJavaClassChooserDialog.getSelected();
    }


    /**
     * 处理选中文本
     *
     * @param event               事件源
     * @param textConvertFunction 文本转换函数（也就是要进行的处理）
     */
    public static void convertSelectText(AnActionEvent event, Function<String, String> textConvertFunction) {
        // 当前编辑器
        Editor editor = getEditor(event);
        // 当前所在文档
        Document document = editor.getDocument();

        // 获取所有选中内容
        List<Caret> caretList = getSelectCarets(editor);

        Runnable task = () -> {
            // 替换选中文本（包括多行）
            for (Caret caret : caretList) {
                // 获取该选中区域的内容
                String selectedText = caret.getSelectedText();
                // 使用转换函数
                selectedText = textConvertFunction.apply(selectedText);
                // 文档替换
                document.replaceString(caret.getSelectionStart(), caret.getSelectionEnd(), selectedText);
            }
        };

        // 执行写入
        ActionUtil.runWriteCommandAction(event.getProject(), task);
    }


    /**
     * 向当前光标处插入文本
     *
     * @param event 包含编辑器信息的AnActionEvent对象
     * @param text  插入的文本
     */
    public static void insertText(AnActionEvent event, String text) {
        Project project = event.getProject();
        Editor editor = ActionUtil.getEditor(event);
        if (Objects.isNull(project)) {
            return;
        }

        // 获取文档
        Document document = editor.getDocument();
        // 要处理多行选中，获取所有选中内容
        List<Caret> caretList = getSelectCarets(editor);
        // 遍历处理
        for (Caret caret : caretList) {
            // 加到原来光标所在的位置后
            ActionUtil.runWriteCommandAction(project, () -> {
                document.insertString(caret.getOffset(), text);
                int offset = caret.getOffset() + text.length();
                caret.moveToOffset(offset);
            });
        }
    }


    /**
     * 导入类到当前类中
     *
     * @param event            AnActionEvent对象，包含当前事件相关信息
     * @param refQualifiedName 要导入的类的全限定名
     */
    public static void importClass(AnActionEvent event, String refQualifiedName) {
        Project project = event.getProject();
        if (Objects.isNull(project)) {
            return;
        }

        JavaCodeStyleManager instance = JavaCodeStyleManager.getInstance(project);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        PsiClass refClass = ActionUtil.findClass(project, refQualifiedName);
        if (Objects.isNull(psiFile) || Objects.isNull(refClass)) {
            return;
        }

        runWriteCommandAction(event.getProject(), () -> instance.addImport((PsiJavaFile) psiFile, refClass));
    }


    /**
     * 将指定类的引用导入给定的PsiClass中。
     *
     * @param project          Java项目
     * @param psiClass         要导入引用的PsiClass
     * @param refQualifiedName 要导入的类的完全限定名
     */
    public static void importClassInClass(Project project, PsiClass psiClass, String refQualifiedName) {
        JavaCodeStyleManager instance = JavaCodeStyleManager.getInstance(project);
        PsiJavaFile containingFile = (PsiJavaFile) psiClass.getContainingFile();
        PsiClass refClass = ActionUtil.findClass(project, refQualifiedName);
        if (Objects.isNull(containingFile) || Objects.isNull(refClass)) {
            return;
        }

        runWriteCommandAction(project, () -> instance.addImport(containingFile, refClass));
    }


    /**
     * 将指定类的引用导入给定的PsiClass中。
     *
     * @param project           Java项目
     * @param psiClass          要导入引用的PsiClass
     * @param refQualifiedNames 要导入的类的完全限定名
     */
    public static void importClassesInClass(Project project, PsiClass psiClass, String... refQualifiedNames) {
        if (Objects.isNull(project) || Objects.isNull(psiClass) || ArrayUtil.isEmpty(refQualifiedNames)) {
            return;
        }

        List<PsiClass> refClasses = new ArrayList<>(refQualifiedNames.length);
        JavaCodeStyleManager instance = JavaCodeStyleManager.getInstance(project);
        PsiJavaFile containingFile = (PsiJavaFile) psiClass.getContainingFile();
        for (String refQualifiedName : refQualifiedNames) {
            PsiClass refClass = ActionUtil.findClass(project, refQualifiedName);
            if (Objects.nonNull(refClass)) {
                refClasses.add(refClass);
            }
        }

        if (Objects.isNull(containingFile) || CollUtil.isEmpty(refClasses)) {
            return;
        }

        runWriteCommandAction(project, () -> {
            for (PsiClass refClass : refClasses) {
                instance.addImport(containingFile, refClass);
            }
        });
    }


    /**
     * 获取编辑器
     *
     * @param event 事件源
     * @return 编辑器
     */
    public static Editor getEditor(AnActionEvent event) {
        return event.getRequiredData(CommonDataKeys.EDITOR);
    }


    /**
     * 获取结构化文件
     *
     * @param event 事件源
     * @return 结构化文件
     */
    public static PsiFile getPsiFile(AnActionEvent event) {
        try {
            return event.getData(CommonDataKeys.PSI_FILE);
        } catch (Throwable e) {
            return null;
        }
    }


    /**
     * 获取结构化元素（可能是方法、变量、等各种）
     *
     * @param event 事件源
     * @return 结构化元素
     */
    public static PsiElement getPsiElement(AnActionEvent event) {
        return event.getData(CommonDataKeys.PSI_ELEMENT);
    }


    /**
     * 获取当前选中的所有内容
     *
     * @param editor 编辑器
     * @return 选中区
     */
    public static List<Caret> getSelectCarets(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();
        // 获取所有选中内容
        return caretModel.getAllCarets();
    }


    /**
     * 获取注解中的指定属性（去除"之后）
     *
     * @param psiAnnotation 注解
     * @param attributeName 属性名
     * @return 属性值
     */
    public static String getMemberValue(PsiAnnotation psiAnnotation, String attributeName) {
        String value = "";
        if (Objects.isNull(psiAnnotation)) {
            return value;
        }

        // 获取注解的属性
        PsiAnnotationMemberValue memberValue = psiAnnotation.findAttributeValue(attributeName);
        if (Objects.isNull(memberValue)) {
            return value;
        }

        // value属性值
        value = memberValue.getText();
        if (StringUtils.isNotBlank(value)) {
            value = value.replace("\"", "");
        }

        return value;
    }


    /**
     * 获取当前选中的行号
     *
     * @param editor   编辑器
     * @param document 文档
     * @return 行号
     */
    public static int getCurrentLine(Editor editor, Document document) {
        SelectionModel selectionModel = editor.getSelectionModel();
        int leadSelectionOffset = selectionModel.getLeadSelectionOffset();
        return document.getLineNumber(leadSelectionOffset);
    }


    /**
     * 获取行号下一行的开始偏移量
     *
     * @param lineNumber 行号
     * @param document   文档
     * @return 下一行的开始偏移量
     */
    public static int getNextLineStartOffset(int lineNumber, Document document) {
        int insertLine = lineNumber + 1;
        return document.getLineStartOffset(insertLine);
    }


    /**
     * 获取行号的当前行的所有内容
     *
     * @param lineNumber 行号
     * @param document   文档
     * @return 内容
     */
    public static String getCurrentLineContent(int lineNumber, Document document) {
        ImmutablePair<Integer, Integer> offset = getCurrentLineOffset(lineNumber, document);
        // 当前行内容
        return document.getText().substring(offset.getLeft(), offset.getRight());
    }


    /**
     * 获取当前光标所在的偏移量（单行选中）
     *
     * @return 偏移量
     */
    public static int getCaretOffset(Editor editor) {
        CaretModel caretModel = editor.getCaretModel();
        // 获取当前光标所在位置
        final Caret primaryCaret = caretModel.getPrimaryCaret();
        // 获取光标的偏移量
        return primaryCaret.getOffset();
    }


    /**
     * 获取该行的开始结束的偏移量（记住是当前行的开始与结束，如果想获取当前光标，需要获取Caret对象，比如caretModel.getPrimaryCaret()）
     *
     * @param lineNumber 行号
     * @param document   文档
     * @return 偏移量，left：开始，right：结束
     */
    public static ImmutablePair<Integer, Integer> getCurrentLineOffset(int lineNumber, Document document) {
        // 当前行开始结束的偏移量
        int startOffset = document.getLineStartOffset(lineNumber);
        int endOffset = document.getLineEndOffset(lineNumber);
        return ImmutablePair.of(startOffset, endOffset);
    }


    public static PsiClass findClassReal(Project project, String qualifiedName) {
        // 现在通过短名获取类
        String className = CommonUtil.qualifiedNameToClassName(qualifiedName);
        GlobalSearchScope globalSearchScope = GlobalSearchScope.allScope(project);
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        @NotNull PsiClass[] psiClasses = shortNamesCache.getClassesByName(className, globalSearchScope);

        if (ArrayUtil.isEmpty(psiClasses)) {
            return null;
        }

        for (PsiClass psiClass : psiClasses) {
            if (Objects.equals(qualifiedName, psiClass.getQualifiedName())) {
                return psiClass;
            }
        }

        return null;
    }


    /**
     * 查找指定项目中的类。
     *
     * @param project       项目对象
     * @param qualifiedName 类的全限定名
     * @return 匹配的PsiClass对象，如果没有找到则返回null
     */
    public static PsiClass findClass(Project project, String qualifiedName) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        return psiFacade.findClass(qualifiedName, GlobalSearchScope.allScope(project));
    }


    /**
     * 查找指定项目中的类。
     *
     * @param project       项目对象
     * @param qualifiedName 类的全限定名
     * @param scope         查找范围
     * @return 匹配的PsiClass对象，如果没有找到则返回null
     */
    public static PsiClass findClass(Project project, String qualifiedName, GlobalSearchScope scope) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        return psiFacade.findClass(qualifiedName, scope);
    }


    /**
     * 查找指定名称的包
     *
     * @param project       项目
     * @param qualifiedName 包名
     * @return 返回找到的包，如果未找到则返回null
     */
    public static PsiPackage findPackage(Project project, String qualifiedName) {
        if (StrUtil.isBlank(qualifiedName)) {
            return null;
        }

        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        return psiFacade.findPackage(qualifiedName);
    }


    /**
     * 是否存在某个依赖
     *
     * @param module      module
     * @param libraryName 依赖名，例如: org.projectlombok:lombok
     * @return true，存在；false，不存在
     */
    public static boolean hasLibrary(Module module, String libraryName) {
        final Ref<Library> result = Ref.create(null);
        OrderEnumerator.orderEntries(module).forEachLibrary(library -> {
            String name = library.getName();
            if (StrUtil.isNotBlank(name) && name.contains(libraryName)) {
                result.set(library);
                return false;
            }
            return true;
        });

        return Objects.nonNull(result.get());
    }


    /**
     * 执行写入命令动作。
     * IO操作不能在UI线程执行，需要异步执行（给class添加元素需要在异步）。
     *
     * @param project 项目
     * @param task    Runnable任务
     */
    public static void runWriteCommandAction(Project project, Runnable task) {
        // IO操作不能在 ui线程执行，需要异步（给class添加元素需要在异步）
        WriteCommandAction.runWriteCommandAction(project, task);
    }


    /**
     * 刷新文件系统
     */
    public static void refreshFileSystem() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }


    /**
     * 将文件转换为PsiDirectory对象
     *
     * @param project 当前项目
     * @param file    要转换的文件
     * @return 转换后的PsiDirectory对象，如果转换失败则返回null
     */
    public static PsiDirectory fileToPsiDirectory(Project project, File file) {
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        VirtualFile virtualFile = fileSystem.refreshAndFindFileByIoFile(file);
        if (Objects.isNull(virtualFile)) {
            return null;
        }

        PsiDirectoryFactory directoryFactory = PsiDirectoryFactory.getInstance(project);
        return directoryFactory.createDirectory(virtualFile);
    }


    @SuppressWarnings("rawtypes")
    public static String createTextWithTemplate(Project project, String templateName, Map map) {
        String result;
        // 获取Where模板
        FileTemplate template = FileTemplateManager.getInstance(project).getInternalTemplate(templateName);
        // 是否作为Live模板
        template.setLiveTemplateEnabled(false);
        try {
            // 构建填充
            result = template.getText(map);
        } catch (IOException e) {
            result = "";
        }

        return result;
    }


    /**
     * 创建换行
     *
     * @param project 项目
     * @return 换行
     */
    public static PsiWhiteSpace createPsiWhiteSpace(Project project) {
        PsiParserFacade psiParserFacade = PsiParserFacade.getInstance(project);
        return (PsiWhiteSpace) psiParserFacade.createWhiteSpaceFromText("""

                   \s
                    \
                """);
    }

    /**
     * 创建换行
     *
     * @param project 项目
     * @return 换行
     */
    public static PsiWhiteSpace createPsiWhiteSpace(Project project, String text) {
        PsiParserFacade psiParserFacade = PsiParserFacade.getInstance(project);
        return (PsiWhiteSpace) psiParserFacade.createWhiteSpaceFromText(text);
    }

    /**
     * 判断是否为引用类型（自己项目中的类）（不包括String、BigDecimal之类的）
     *
     * @param psiType 类型
     * @return true，引用类型；false，不为引用类型
     */
    public static boolean isReferenceType(PsiType psiType) {
        // 不为引用类型
        if (!(psiType instanceof PsiClassReferenceType)) {
            return false;
        }

        // 全限定名（基本类型就只有基本类型名 long、int）
        String canonicalText = psiType.getCanonicalText();
        // 判断是否为java包其他类
        return !StrUtil.startWith(canonicalText, "java.");
    }


    /**
     * 是否选中
     *
     * @param event 事件源
     * @return true -> 选中；false -> 未选中
     */
    public static boolean isSelected(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (Objects.isNull(editor)) {
            return false;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        return StringUtils.isNotBlank(selectionModel.getSelectedText());
    }


    /**
     * 是否处于Java文件
     *
     * @param event 事件源
     * @return true -> 处于；false -> 不处于
     */
    public static boolean isJavaFile(AnActionEvent event) {
        // 获取当前选中的 PsiClass
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        return Objects.nonNull(psiFile) && (psiFile.getFileType() instanceof JavaFileType);
    }

    /**
     * 是否处于Java文件
     *
     * @param psiFile 事件源
     * @return true -> 处于；false -> 不处于
     */
    public static boolean isJavaFile(PsiFile psiFile) {
        // 获取当前选中的 PsiClass
        return Objects.nonNull(psiFile) && (psiFile.getFileType() instanceof JavaFileType);
    }


    /**
     * 是否处于XML文件
     *
     * @param event 事件源
     * @return true -> 处于；false -> 不处于
     */
    public static boolean isXmlFile(AnActionEvent event) {
        // 获取当前选中的 PsiClass
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        return psiFile instanceof XmlFile;
    }


    public static boolean isWrite(PsiFile psiFile) {
        return Objects.nonNull(psiFile) && psiFile.isWritable();
    }

    public static boolean isWrite(AnActionEvent event) {
        PsiFile psiFile = ActionUtil.getPsiFile(event);
        return Objects.nonNull(psiFile) && psiFile.isWritable();
    }

    // ================= 暂时不管用的方法

    @Deprecated
    public static void reformatJavaCode(PsiElement element) {
        CodeStyleManager.getInstance(element.getProject()).reformat(element);
    }


    /**
     * 设置剪贴板内容
     *
     * @param content 要设置到剪贴板中的字符串内容
     */
    public static void setClipboard(String content) {
        CopyPasteManager.getInstance().setContents(new SimpleTransferable(content, DataFlavor.stringFlavor));
    }


    /**
     * 将虚拟文件转换为PsiFile
     *
     * @param project     项目
     * @param virtualFile 虚拟文件
     * @return PsiFile 文件对象
     */
    public static PsiFile virtualFileToPsiFile(Project project, VirtualFile virtualFile) {
        return PsiManager.getInstance(project).findFile(virtualFile);
    }


    /**
     * 根据文件扩展名在项目中查找所有文件
     *
     * @param project     当前项目
     * @param ext         文件扩展名
     * @param searchScope 搜索范围
     * @return 返回所有符合条件的VirtualFile对象集合
     */
    public static Collection<VirtualFile> findFilesByExt(Project project, String ext, GlobalSearchScope searchScope) {
        return FilenameIndex.getAllFilesByExt(project, ext, searchScope);
    }


    /**
     * 根据类名获取对应的PsiPackage
     *
     * @param project       项目对象
     * @param qualifiedName 类名（包括包名）
     * @return 对应的PsiPackage对象
     */
    public static PsiPackage getPsiPackageByClass(Project project, String qualifiedName) {
        return StrUtil.isBlank(qualifiedName) ? null : JavaPsiFacade.getInstance(project).findPackage(qualifiedName);
    }

    public static void customize(EditorEx editor) {
        // 拼写检查
        EditorCustomization enabledCustomization = SpellCheckingEditorCustomizationProvider.getInstance().getDisabledCustomization();
        if (Objects.nonNull(enabledCustomization)) {
            enabledCustomization.customize(editor);
        }

        // 水平横幅条
        HorizontalScrollBarEditorCustomization horizontalScrollBarEditorCustomization = HorizontalScrollBarEditorCustomization.ENABLED;
        horizontalScrollBarEditorCustomization.customize(editor);

        // 垂直横幅条
        editor.setVerticalScrollbarVisible(true);

        // 取消右侧红线
        ErrorStripeEditorCustomization errorStripeEditorCustomization = ErrorStripeEditorCustomization.DISABLED;
        errorStripeEditorCustomization.customize(editor);

        // 单行模式
        OneLineEditorCustomization oneLineEditorCustomization = OneLineEditorCustomization.DISABLED;
        oneLineEditorCustomization.customize(editor);
    }


    public static void showGotItTip(String id, String title, String message, Icon icon, int timeout, JComponent jComponent,
                                    kotlin.jvm.functions.Function2<java.awt.Component, Object, java.awt.Point> function2) {
        new GotItTooltip(id, message, null)
                .withHeader(title)
                .withTimeout(timeout)
                .withShowCount(Integer.MAX_VALUE)
                .withIcon(icon)
                .show(jComponent, function2);
    }


    public static void showGotItTip(String id, String title, String message, String buttonLabel, Icon icon, JComponent jComponent,
                                    kotlin.jvm.functions.Function2<java.awt.Component, Object, java.awt.Point> function2) {
        new GotItTooltip(id, message, null)
                .withHeader(title)
                .withShowCount(Integer.MAX_VALUE)
                .withButtonLabel(buttonLabel)
                .withIcon(icon)
                .show(jComponent, function2);
    }


    /**
     * 插件是否安装并启用
     *
     * @param pluginId                 插件ID
     * @param pluginClassQualifiedName 插件其中的类全限定名，传null不验证class
     * @return true：安装并启用；false：未启用或未安装
     */
    public static boolean isPluginInstalledAndEnabled(String pluginId, String pluginClassQualifiedName) {
        PluginId pluginIdObj = PluginId.getId(pluginId);
        // 已安装
        return PluginManagerCore.isPluginInstalled(pluginIdObj)
                // 已启用
                && !PluginManagerCore.isDisabled(pluginIdObj)
                // 继续验证
                && (StrUtil.isBlank(pluginClassQualifiedName) || existPlugin(pluginClassQualifiedName));
    }


    public static boolean existPlugin(String pluginClassQualifiedName) {
        try {
            // 这个类加载用的是idea应用内置的jdk
            Class.forName(pluginClassQualifiedName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static List<Module> getModuleList(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        return Arrays.stream(modules).toList();
    }


    public static VirtualFile getVfByPackage(PsiPackage psiPackage) {
        if (Objects.nonNull(psiPackage)) {
            PsiDirectory[] directories = psiPackage.getDirectories();
            if (ArrayUtil.isNotEmpty(directories)) {
                PsiDirectory directory = directories[0];
                return directory.getVirtualFile();
            }
        }

        return null;
    }



    public static void openHtmlEditor(Project project, String title, String html) {
        // 打开一个HTML展示Tab
        HTMLEditorProvider.openEditor(project, title, html);
    }


    public static void openToolWindow(Project project, String toolWindowId) {
        ToolWindowManager instance = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = instance.getToolWindow(toolWindowId);
        if (Objects.nonNull(toolWindow)) {
            toolWindow.show();
        }
    }


    /**
     * 获取工具窗口中的页面实例
     *
     * @param project      项目
     * @param toolWindowId 工具窗id
     */
    public static JComponent getPageInToolWindow(Project project, String toolWindowId, int pageIndex) {
        ToolWindowManager instance = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = instance.getToolWindow(toolWindowId);

        if (Objects.nonNull(toolWindow)) {
            ContentManager contentManager = toolWindow.getContentManager();
            Content[] contents = contentManager.getContents();
            Content content = contents[pageIndex];
            return content.getComponent();
        }

        return null;
    }


    public static void showPopupBalloon(String message, long fadeoutTime, PopupTypeEnum popupTypeEnum,
                                        JComponent component, Balloon.Position position) {

        String iconPath = "/icons/okNew.svg";

        switch (popupTypeEnum) {
            case info -> {
                iconPath = "/icons/okNew.svg";
            }
            case waring -> {
                iconPath = "/icons/waring.svg";
            }
            case error -> {
                iconPath = "/icons/errorNew.svg";
            }
        }

        JBLabel label = new JBLabel(message, UIUtil.ComponentStyle.REGULAR);
        label.setIcon(IconLoader.getIcon(iconPath, ActionUtil.class.getClassLoader()));

        JPanel panel = new JPanel(new BorderLayout(JBUI.scale(4), JBUI.scale(2)));
        panel.add(label, BorderLayout.CENTER);

        BalloonBuilder balloonBuilder = JBPopupFactory.getInstance().createDialogBalloonBuilder(panel, null)
                .setHideOnAction(true)
                .setShadow(true)
                .setHideOnClickOutside(true)
                .setHideOnFrameResize(true)
                .setFadeoutTime(fadeoutTime)
                .setCloseButtonEnabled(false)
                .setHideOnKeyOutside(true);

        Balloon balloon = balloonBuilder.createBalloon();

        RelativePoint relativePoint = new RelativePoint(component, new Point(1, component.getHeight()));
        balloon.show(relativePoint, position);
    }


    public static void showPopupBalloon(String message, Editor editor, PopupTypeEnum popupTypeEnum) {
        switch (popupTypeEnum) {
            case info -> {
                HintManager.getInstance().showInformationHint(editor, message);
            }
            case waring, error -> {
                HintManager.getInstance().showErrorHint(editor, message, HintManager.ABOVE);
            }
        }
    }

    // 通过这个方式可以直接格式化代码，所以模版里可以不用太在意格式信息
    // ReformatCodeProcessor reformatCodeProcessor = new ReformatCodeProcessor(e.getProject(), file, null, false);
    // reformatCodeProcessor.run();


}
