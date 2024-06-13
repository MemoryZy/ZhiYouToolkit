// package cn.zhiyou.action;
//
// import cn.hutool.core.util.ArrayUtil;
// import cn.hutool.core.util.StrUtil;
// import cn.zhiyou.ricotoolbox.enums.SqlTagEnum;
// import cn.zhiyou.ricotoolbox.util.ActionUtil;
// import cn.zhiyou.ricotoolbox.util.NotificationUtil;
// import com.intellij.notification.NotificationType;
// import com.intellij.openapi.actionSystem.ActionUpdateThread;
// import com.intellij.openapi.actionSystem.AnAction;
// import com.intellij.openapi.actionSystem.AnActionEvent;
// import com.intellij.openapi.actionSystem.PlatformDataKeys;
// import com.intellij.openapi.project.Project;
// import com.intellij.psi.PsiClass;
// import com.intellij.psi.PsiElement;
// import com.intellij.psi.PsiFile;
// import com.intellij.psi.util.PsiTreeUtil;
// import com.intellij.psi.xml.XmlAttribute;
// import com.intellij.psi.xml.XmlFile;
// import com.intellij.psi.xml.XmlTag;
// import org.jetbrains.annotations.NotNull;
//
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Objects;
//
// /**
//  * 将XML文件中的SQL转换为可执行的SQL语句
//  *
//  * @author Memory
//  * @since 2023/12/1
//  */
// public class ConvertExecutableSqlAction extends AnAction {
//
//     @Override
//     public void actionPerformed(@NotNull AnActionEvent event) {
//         Project project = event.getProject();
//         if (Objects.isNull(project)) {
//             return;
//         }
//
//         PsiFile psiFile = event.getData(PlatformDataKeys.PSI_FILE);
//         PsiElement element = ActionUtil.getPsiElementByOffset(event);
//         // mapperStatement标签
//         XmlTag msTag = PsiTreeUtil.getParentOfType(element, XmlTag.class);
//
//         if (!(psiFile instanceof XmlFile xmlFile)) {
//             NotificationUtil.notifyApplication("当前未处于Xml文档内！", NotificationType.WARNING, project);
//             return;
//         }
//
//         // 获取根标签<mapper>
//         XmlTag rootTag = xmlFile.getRootTag();
//         if (Objects.isNull(rootTag) || !Objects.equals(rootTag.getName(), "mapper")) {
//             NotificationUtil.notifyApplication("当前文档不属于MyBatis-Xml！", NotificationType.WARNING, project);
//             return;
//         }
//
//         if (Objects.isNull(msTag)) {
//             NotificationUtil.notifyApplication("您未选中MapperStatement！", NotificationType.WARNING, project);
//             return;
//         }
//
//         // select|insert|update|delete
//         SqlTagEnum sqlTagEnum = SqlTagEnum.match(msTag.getName());
//         if (Objects.isNull(sqlTagEnum)) {
//             NotificationUtil.notifyApplication("您未选中MapperStatement！", NotificationType.WARNING, project);
//             return;
//         }
//
//         // 获取该标签内的所有内容
//         String tagContent = msTag.getValue().getText();
//         if (StrUtil.isBlank(tagContent)) {
//             NotificationUtil.notifyApplication("未找到可解析的SQL语句！", NotificationType.WARNING, project);
//             return;
//         }
//
//         // 获取namespace
//         XmlAttribute namespaceAttribute = rootTag.getAttribute("namespace");
//         String namespace = Objects.isNull(namespaceAttribute) ? "" : namespaceAttribute.getValue();
//         PsiClass namespaceClass = ActionUtil.findClass(event.getProject(), namespace);
//
//         // if () {
//         //
//         // }
//
//         // 执行
//         String executableSql = getExecutableSql(tagContent, msTag, sqlTagEnum, rootTag);
//
//         if (StrUtil.isBlank(executableSql)) {
//             NotificationUtil.notifyApplication("SQL语句生成失败！", NotificationType.WARNING, project);
//             return;
//         }
//
//         // 获取可执行SQL，并添加至剪贴板
//         ActionUtil.setClipboard(executableSql);
//         // 给通知
//         NotificationUtil.notifyApplication("已拷贝到剪贴板！", NotificationType.INFORMATION, project);
//     }
//
//
//     @Override
//     public @NotNull ActionUpdateThread getActionUpdateThread() {
//         return ActionUpdateThread.BGT;
//     }
//
//     // @Override
//     // public void update(@NotNull AnActionEvent e) {
//     //     PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
//     //
//     //     boolean visible = false;
//     //     if (psiFile instanceof XmlFile xmlFile) {
//     //         // 获取根标签<mapper>
//     //         XmlTag rootTag = xmlFile.getRootTag();
//     //         if (Objects.nonNull(rootTag)) {
//     //             // 获取当前光标选择的行内容
//     //             Editor editor = ActionUtil.getEditor(e);
//     //             Document currentDocument = editor.getDocument();
//     //             int currentLine = ActionUtil.getCurrentLine(editor, currentDocument);
//     //             String currentLineContent = ActionUtil.getCurrentLineContent(currentLine, currentDocument);
//     //
//     //             // 判断当前行内容是否符合要求，如果选择的行不包含 id
//     //             if (!(StrUtil.isBlank(currentLineContent) || !currentLineContent.contains("id") ||
//     //                     // 或不包含4大SQL标签
//     //                     (!currentLineContent.contains("<select ") && !currentLineContent.contains("<insert ")
//     //                             && !currentLineContent.contains("<update ") && !currentLineContent.contains("<delete ")))) {
//     //                 visible = true;
//     //             }
//     //         }
//     //     }
//     //
//     //     e.getPresentation().setEnabledAndVisible(visible);
//     // }
//
//
//     /**
//      * 获取可执行的SQL语句。
//      *
//      * @param tagContent 包含SQL语句的字符串
//      * @param tag        SQL语句所处标签
//      * @param tagEnum    SQL语句类型
//      * @param rootTag    当前XML标签的根标签mapper
//      * @return 可执行的SQL语句
//      */
//     private String getExecutableSql(String tagContent, XmlTag tag, SqlTagEnum tagEnum, XmlTag rootTag) {
//         /*
//         select ts.id              as id,
//                ts.airport_code    as airportCode,
//                ts.unit_name       as unitValue,
//                ts.time_parameters as timeParameters,
//                am.model_name      as model
//                     from tcoc_sch_transfer_station_set ts
//                 inner join tcoc_sch_trs_am_correlation tc on ts.id = tc.trs_id
//                 inner join tcoc_sch_aircraft_model am on am.id = tc.am_id
//                 <where>
//                     <if test="query.airportCode != null and query.airportCode.trim() != ''">
//                         and ts.airport_code = #{query.airportCode}
//                     </if>
//                     <if test="query.unitValue != null and query.unitValue.trim() != ''">
//                         and ts.unit_name = #{query.unitValue}
//                     </if>
//                     <if test="query.model != null and query.model.trim() != ''">
//                         and am.model_name = #{query.model}
//                     </if>
//
//                     and (ts.del_flag = 0 and tc.del_flag = 0 and am.del_flag = 0)
//                 </where>
//             order by ts.airport_code, ts.unit_name
//             limit #{startIndex,jdbcType=BIGINT}, #{endIndex,jdbcType=BIGINT}
//          */
//
//         // 存储标签内引入的SQL片段 (key: <include refid="query"/>，value: SQL片段)
//         Map<String, String> includeMap = getIncludeMap(tag, rootTag);
//
//         // 替换<include>标签为具体的SQL片段
//         for (Map.Entry<String, String> entry : includeMap.entrySet()) {
//             tagContent = tagContent.replace(entry.getKey(), entry.getValue());
//         }
//
//         if (Objects.equals(SqlTagEnum.UPDATE, tagEnum)) {
//             // 获取当前标签内的<set>标签
//
//         }
//
//
//         // 获取当前标签内的<if>标签
//
//         // 获取当前标签内的<where>标签
//
//         // 获取<where>标签内的<if>标签
//
//
//         if (StrUtil.containsAny(tagContent, "#", "$")) {
//             // 找到对应的Mapper接口，从里面取方法参数，得到类型进行替换
//
//         }
//
//
//         return null;
//     }
//
//
//     /**
//      * 存储标签内引入的SQL片段 (key: <include refid="query"/>，value: SQL片段)
//      *
//      * @param tag     当前标签
//      * @param rootTag 根节点标签
//      * @return 包含<include>标签的Map
//      */
//     private Map<String, String> getIncludeMap(XmlTag tag, XmlTag rootTag) {
//         Map<String, String> includeMap = new HashMap<>();
//
//         // 如果其中含有<include 标签，则将该标签内容获取，并替换到原SQL中
//         XmlTag[] includeTags = tag.findSubTags("include");
//         if (ArrayUtil.isNotEmpty(includeTags)) {
//             for (XmlTag includeTag : includeTags) {
//                 // 引入标签完整内容
//                 String includeFullText = includeTag.getText().trim();
//
//                 // 获取include标签的refid属性
//                 XmlAttribute refid = includeTag.getAttribute("refid");
//                 if (Objects.isNull(refid)) {
//                     continue;
//                 }
//                 // 引入的SQL片段id名
//                 String refidValue = refid.getValue();
//                 // 去根节点下寻找SQL片段标签
//                 XmlTag[] sqlFragmentTags = rootTag.findSubTags("sql");
//                 if (ArrayUtil.isEmpty(sqlFragmentTags)) {
//                     continue;
//                 }
//
//                 for (XmlTag sqlFragmentTag : sqlFragmentTags) {
//                     // 获取标签内的id属性
//                     XmlAttribute id = sqlFragmentTag.getAttribute("id");
//                     // 判断用户选择的行id是否与当前遍历的id一致，不一致则跳过
//                     if (Objects.isNull(id) || !Objects.equals(refidValue, id.getValue())) {
//                         continue;
//                     }
//                     // 获取该标签内的所有内容
//                     String sqlTagContent = sqlFragmentTag.getValue().getText().trim();
//                     // 添加至Map
//                     includeMap.put(includeFullText, sqlTagContent);
//                 }
//             }
//         }
//
//         return includeMap;
//     }
// }
