package cn.zhiyou.constant;

/**
 * @author wcp
 * @since 2024/2/6
 */
public class PluginHelpConstant {

    public static final String helpHtmlText = """
<!DOCTYPE html>
<html lang="en">
<head>
    <title>What's New</title>
    <meta charset="utf-8" />
    <meta http-equiv="x-ua-compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, maximum-scale=1" />

    <style>
        *, *::after {
            box-sizing: border-box;
        }

        html, body, p {
            margin: 0;
            padding: 0;
        }
        html, body {
            height: 100%;
        }
        body {
            background-color: #fff;
        }

        .container {
            box-sizing: border-box;
            width: 100%;
            max-width: 1276px;
            margin-right: auto;
            margin-left: auto;
            padding-right: 22px;
            padding-left: 22px;
        }
        .content {
            width: calc(100% / 12 * 6);
            margin-left: calc(100% / 12 * 3);
        }

        .text {
            letter-spacing: normal;
            color: rgba(39, 40, 44, 0.7);
            font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Droid Sans', 'Helvetica Neue', Arial, sans-serif;
            font-size: 15px;
            font-weight: normal;
            font-style: normal;
            font-stretch: normal;
            line-height: 1.6;
            text-indent: 1em;
        }

        .title {
            letter-spacing: normal;
            color: #27282c;
            font-family: -apple-system, Helvetica, system-ui, BlinkMacSystemFont, Segoe UI, Roboto, Oxygen, Ubuntu, Cantarell, Droid Sans, Helvetica Neue, Arial, sans-serif;
            font-weight: bold;
            font-style: normal;
            font-stretch: normal;
        }

        .title_h2 {
            font-size: 28px;
            line-height: 1.3;
        }

        .offset-12 {
            margin-top: 12px;
        }
        .offset-24 {
            margin-top: 20px;
        }
        .offset-6 {
            margin-top: 6px;
        }
        .list {
            color: #27282c;
        }

        /*noinspection CssUnusedSymbol*/
        .link {
            outline: none;
            cursor: pointer;
            font-size: inherit;
            line-height: inherit;
            border-bottom: 1px solid transparent;
        }
        /*noinspection CssUnusedSymbol*/
        .link, .link:hover {
            text-decoration: none;
        }
        /*noinspection CssUnusedSymbol*/
        .link:hover {
            border-bottom-color: currentColor;
        }
        /*noinspection CssUnusedSymbol*/
        .link, .link:hover, .link:active, .link:focus {
            color: #167dff;
        }

        .section {
            padding-bottom: 48px;
            padding-top: 1px;
            background: #fff;
        }

        /*noinspection CssUnusedSymbol*/
        .theme-dark, .theme-dark .section {
            background: #27282c;
        }
        .theme-dark .title {
            color: #fff;
        }
        .theme-dark .text {
            color: rgba(255, 255, 255, 0.60);
            text-indent: 1em;
        }
        .theme-dark .list {
            color: rgba(255, 255, 255, 0.60);
        }
        /*noinspection CssUnusedSymbol*/
        .theme-dark .link {
            color: rgb(76, 166, 255);
        }

        @media screen and (max-width: 1276px) {
            .container {
                max-width: 996px;
                padding-right: 22px;
                padding-left: 22px;
            }
            .content {
                width: calc(100% / 12 * 8);
                margin-left: calc(100% / 12 * 2);
            }
        }
        @media screen and (max-width: 1000px) {
            .container {
                max-width: 100%;
            }
            .content {
                width: calc(100% / 12 * 10);
                margin-left: calc(100% / 12 * 1);
            }
        }
        @media screen and (max-width: 640px) {
            .container {
                padding-right: 16px;
                padding-left: 16px;
            }
            .content {
                width: calc(100% / 12 * 12);
                margin-left: calc(100% / 12 * 0);
            }
            .offset-12 {
                margin-top: 8px;
                text-indent: 1em;
            }
            .offset-24 {
                margin-top: 12px;
                text-indent: 1em;
            }
        }
    </style>
</head>
<body class="__THEME__">
<section class="section">
    <div class="container">
        <div class="content">
            <h2 class="title title_h2 offset-24">代码笔记</h2>
            <p class="text offset-12">此功能在IDEA界面右侧边栏，名称叫 “Code Note”，支持新增、删除、修改、导入、导出等功能。</p>
            <ul>
                <li class="list"><p class="text offset-6">右侧边栏 Code Note 工具窗口 -> 新增按钮</p></li>
                <li class="list"><p class="text offset-6">代码编辑区内选中代码 -> 鼠标右键 -> Save Notes。</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">JSON转JavaBean</h2>
            <p class="text offset-12">此功能支持嵌套属性、JSON集合等。</p>
            <ul>
                <li class="list"><p class="text offset-6">IDEA左侧文件区 -> 鼠标右键 -> New -> Class By JSON</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">JavaBean转Json</h2>
            <p class="text offset-12">支持嵌套属性，支持FastJson、Jackson注解。</p>
            <ul>
                <li class="list"><p class="text offset-6">Java类中(代码编辑器区) -> 鼠标右键 -> Convert to JSON</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">驼峰、下划线转换及中文转拼音</h2>
            <p class="text offset-12">选中文本可进行转换，中文转拼音需要选中中文。</p>
            <ul>
                <li class="list"><p class="text offset-6">Java类中(代码编辑器区)选中文本 -> 鼠标右键 -> Convert String</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">单行注释转换为文档注释</h2>
            <p class="text offset-12">此功能可以将单行注释转为文档注释，使用时，光标需要在存在单行注释的行中。</p>
            <ul>
                <li class="list"><p class="text offset-6">光标选在单行注释的行中 -> 鼠标右键 -> Line To Doc</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">生成MyBatis-ResultMap</h2>
            <p class="text offset-12">此功能可以生成ResultMap。生成时，光标焦点处于Java类中或XML文件中都可以使用该功能。</p>
            <p class="text offset-12">如果焦点处于Java类中，那么直接点击功能后，无需选择类，在弹出的窗口中输入Select语句；</p>
            <p class="text offset-12">如果焦点处于XML文件中，那么点击功能后，需要选择一个类，在弹出的窗口中输入Select语句。</p>
            <p class="text offset-12" style="color: #167dff">选择类的作用是将类字段与Select语句做映射</p>
            <p class="text offset-24">示例：</p>
            <p class="text offset-12">&nbsp;&nbsp;类字段：[id, name, departmentId]</p>
            <p class="text offset-12">&nbsp;&nbsp;Select语句：[select id, name, departmentId]</p>
            <p class="text offset-24">功能入口：</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Other Operations</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">生成MyBatis-Where</h2>
            <p class="text offset-12">此功能可以帮助生成Where标签，减少重复工作。与生成ResultMap的功能一样，使用时需要在Java类中或XML中。</p>
            <p class="text offset-12">可以自己指定要生成出的列名、属性名等，默认只判断【等于】。</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Other Operations</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">Getter、Setter方法生成与类字段映射</h2>
            <p class="text offset-12">顾名思义，有四个功能：生成所有Getter、Setter、带默认值的Setter、类与类字段映射。</p>
            <p class="text offset-12">当焦点处于方法内时，可调用类与类字段映射功能，有三个选择：</p>
            <ul>
                <li class="list"><p class="text offset-6">插件自动获取方法的出参及入参，取两个类中的相同字段进行映射</p></li>
                <li class="list"><p class="text offset-6">插件自动获取方法的出参，用户选择一个类，取两个类中的相同字段进行映射</p></li>
            </ul>

            <p class="text offset-12">当焦点处于方法内变量时，可以基于这个变量的类型来生成信息，有下列几种选择：</p>
            <ul>
                <li class="list"><p class="text offset-6">插件基于变量的类型生成该变量的所有Get方法及Get方法的结果</p></li>
                <li class="list"><p class="text offset-6">插件基于变量的类型生成该变量的所有Set方法</p></li>
                <li class="list"><p class="text offset-6">插件基于变量的类型生成该变量的所有Set方法，并附上默认值</p></li>
                <li class="list"><p class="text offset-6">插件基于变量的类型，用户再选择一个类，组成映射条件，生成映射</p></li>
            </ul>
            <p class="text offset-24">功能入口：</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Mapping (Get/Set)</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">生成序列化id (serialVersionUID)</h2>
            <p class="text offset-12">此功能可以帮助生成序列化id，自定义算法计算。</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Generate... -> SerialVersionUID (序列化)</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">复制Spring接口完整路径</h2>
            <p class="text offset-12">此功能可以帮助一键复制Spring接口的完整链接，前提是光标要处于Spring接口方法内。</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Copy Rest Url (完整路径)</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">Json窗口展示</h2>
            <p class="text offset-12">此功能是方便那些需要经常看JSON，格式化JSON却每次要去在线网格式化的开发者。</p>
            <p class="text offset-12">有时JSON文本总是会被包裹在其余不属于JSON的文本中，右键点击JSON格式化可以去除这些文本，保留JSON并格式化。</p>
            <p class="text offset-12">想要压缩JSON时，可以右键点击JSON压缩进行JSON压缩。</p>
            <ul>
                <li class="list"><p class="text offset-6">右侧边栏 Json Panel 工具窗口</p></li>
                <li class="list"><p class="text offset-6">右侧边栏 Json Panel 工具窗口 -> 鼠标右键 -> JSON格式化</p></li>
                <li class="list"><p class="text offset-6">右侧边栏 Json Panel 工具窗口 -> 鼠标右键 -> JSON压缩</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">时间戳与时间转换工具</h2>
            <p class="text offset-12">此功能是可以做到时间戳与时间的转换，时间可以是不合法的时间，例如2024-13-10，这样的话年份就会进1.......</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Other Operations</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">MyBatis逆向生成</h2>
            <p class="text offset-12">此功能是帮助减少重复工作，通过选择表就可以做到生成Mapper接口、XML文件、实体类...</p>
            <p class="text offset-12">此功能提供了两个入口：</p>
            <ul>
                <li class="list"><p class="text offset-6">一个是通过Database工具选择要生成的表(支持多选)，然后右键，选择MyBatis Mapper (逆向)</p></li>
                <li class="list"><p class="text offset-6">一个是在IDEA文件区 -> New -> MyBatis Mapper (逆向)，通过这里进入也可，但是要选择表来进行生成(支持多选)</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">字段生成指定注解及注释</h2>
            <p class="text offset-12">此功能可以帮助生成JavaBean中字段上的注解及注释，有四个选择：</p>
            <ul>
                <li class="list"><p class="text offset-6">生成MyBatis Plus注解</p></li>
                <li class="list"><p class="text offset-6">生成Swagger注解</p></li>
                <li class="list"><p class="text offset-6">生成FastJson注解</p></li>
                <li class="list"><p class="text offset-6">生成Jackson注解</p></li>
            </ul>
            <p class="text offset-12">而生成Swagger、FastJson、Jackson注解又提供了几个按钮：</p>
            <ul>
                <li class="list"><p class="text offset-6">驼峰 (示例：属性名称 employee_name，注解值 employeeName)</p></li>
                <li class="list"><p class="text offset-6">下划线 (示例：属性名称 employeeId，注解值 employee_id)</p></li>
                <li class="list"><p class="text offset-6">注释框转注解值框 (示例：字段注释：“名称”，注解值：“名称”)</p></li>
            </ul>
            <p class="text offset-24">功能入口：</p>
            <ul>
                <li class="list"><p class="text offset-6">Java类中(代码编辑器区，且存在字段) -> 鼠标右键 -> Create Annotation</p></li>
            </ul>
            <p class="text offset-12" style="color: red">生成MyBatis Plus注解与MyBatis逆向生成功能一样，是需要连接数据库的。</p>
            <p class="text offset-12" style="color: red">调用此功能时，如果是旗舰版，则可以选择你在Database中配置的数据源进行生成。</p>

            <br/>

            <h2 class="title title_h2 offset-24">Jasypt加解密工具</h2>
            <p class="text offset-12">此功能未完全实现，目前只支持普通的StandardPBEStringEncryptor。</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Other Operations</p></li>
            </ul>
            <br/>

            <h2 class="title title_h2 offset-24">JSON/XML转换工具</h2>
            <p class="text offset-12">JSON/XML的转换。</p>
            <ul>
                <li class="list"><p class="text offset-6">鼠标右键 -> Other Operations</p></li>
            </ul>

        </div>
    </div>
</section>
</body>
</html>
            """;


}
