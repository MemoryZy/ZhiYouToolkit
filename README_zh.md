<div align="center">

# [![ZhiYouToolkit](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/src/main/resources/META-INF/pluginIcon@30x30.svg)](https://github.com/MemoryZy/ZhiYouToolkit) **ZhiYouToolkit**

[English](./README.md) / 简体中文

知游工具，一款基于 IntelliJ IDEA 的集成工具插件。它汇集了日常开发中常用的各种功能，旨在帮助开发者简化重复工作、提高代码开发效率和质量。

[![Homepage](https://img.shields.io/badge/Homepage-ZhiYouToolkit-0db7ed.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTEwIDE5di01aDR2NWMwIC41NS40NSAxIDEgMWgzYy41NSAwIDEtLjQ1IDEtMXYtN2gxLjdjLjQ2IDAgLjY4LS41Ny4zMy0uODdMMTIuNjcgMy42Yy0uMzgtLjM0LS45Ni0uMzQtMS4zNCAwbC04LjM2IDcuNTNjLS4zNC4zLS4xMy44Ny4zMy44N0g1djdjMCAuNTUuNDUgMSAxIDFoM2MuNTUgMCAxLS40NSAxLTEiLz48L3N2Zz4=)](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)
&nbsp;
[![Release](https://img.shields.io/github/v/release/MemoryZy/ZhiYouToolkit?style=Plastic&label=Release&color=d05ce3&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIj48cGF0aCBkPSJNMjQgMHYyNEgwVjB6TTEyLjU5MyAyMy4yNThsLS4wMTEuMDAybC0uMDcxLjAzNWwtLjAyLjAwNGwtLjAxNC0uMDA0bC0uMDcxLS4wMzVjLS4wMS0uMDA0LS4wMTktLjAwMS0uMDI0LjAwNWwtLjAwNC4wMWwtLjAxNy40MjhsLjAwNS4wMmwuMDEuMDEzbC4xMDQuMDc0bC4wMTUuMDA0bC4wMTItLjAwNGwuMTA0LS4wNzRsLjAxMi0uMDE2bC4wMDQtLjAxN2wtLjAxNy0uNDI3Yy0uMDAyLS4wMS0uMDA5LS4wMTctLjAxNy0uMDE4bS4yNjUtLjExM2wtLjAxMy4wMDJsLS4xODUuMDkzbC0uMDEuMDFsLS4wMDMuMDExbC4wMTguNDNsLjAwNS4wMTJsLjAwOC4wMDdsLjIwMS4wOTNjLjAxMi4wMDQuMDIzIDAgLjAyOS0uMDA4bC4wMDQtLjAxNGwtLjAzNC0uNjE0Yy0uMDAzLS4wMTItLjAxLS4wMi0uMDItLjAyMm0tLjcxNS4wMDJhLjAyMy4wMjMgMCAwIDAtLjAyNy4wMDZsLS4wMDYuMDE0bC0uMDM0LjYxNGMwIC4wMTIuMDA3LjAyLjAxNy4wMjRsLjAxNS0uMDAybC4yMDEtLjA5M2wuMDEtLjAwOGwuMDA0LS4wMTFsLjAxNy0uNDNsLS4wMDMtLjAxMmwtLjAxLS4wMXoiLz48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTIwLjI0NSAxNC43NWMuOTM1LjYxNC44OTIgMi4wMzctLjEyOSAyLjU3NmwtNy4xODEgMy43OTZhMiAyIDAgMCAxLTEuODcgMGwtNy4xODEtMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS44NyAwbDcuMTgxLTMuNzk2YTEuNTkgMS41OSAwIDAgMCAuMTMtLjA3NlptMC00YTEuNSAxLjUgMCAwIDEgMCAyLjUwMWwtLjEyOS4wNzVsLTcuMTgxIDMuNzk2YTIgMiAwIDAgMS0xLjcwNy4wNzdsLS4xNjItLjA3N2wtNy4xODItMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS43MDguMDc3bC4xNjItLjA3N2w3LjE4MS0zLjc5NmExLjU5IDEuNTkgMCAwIDAgLjEzLS4wNzZabS03LjMxLTcuODcybDcuMTgxIDMuNzk2YzEuMDY2LjU2MyAxLjA2NiAyLjA5IDAgMi42NTJsLTcuMTgxIDMuNzk3YTIgMiAwIDAgMS0xLjg3IDBMMy44ODQgOS4zMjZjLTEuMDY2LS41NjMtMS4wNjYtMi4wODkgMC0yLjY1Mmw3LjE4MS0zLjc5NmEyIDIgMCAwIDEgMS44NyAwIi8+PC9nPjwvc3ZnPg==)](https://github.com/MemoryZy/ZhiYouToolkit/releases)
&nbsp;
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/MemoryZy/ZhiYouToolkit/verification.yml?branch=main&style=plastic&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0ibTE3LjE1IDIwLjdsLTYuMDUtNi4xcS0uNS4yLTEuMDEyLjNUOSAxNXEtMi41IDAtNC4yNS0xLjc1VDMgOXEwLS45LjI1LTEuNzEydC43LTEuNTM4TDcuNiA5LjRsMS44LTEuOGwtMy42NS0zLjY1cS43MjUtLjQ1IDEuNTM4LS43VDkgM3EyLjUgMCA0LjI1IDEuNzVUMTUgOXEwIC41NzUtLjEgMS4wODh0LS4zIDEuMDEybDYuMSA2LjA1cS4zLjMuMy43MjV0LS4zLjcyNWwtMi4xIDIuMXEtLjMuMy0uNzI1LjN0LS43MjUtLjMiLz48L3N2Zz4%3D&label=Build)](https://github.com/MemoryZy/ZhiYouToolkit/actions/workflows/Compatibility.yml)
&nbsp;
[![Java](https://img.shields.io/badge/Lang-Java-ff5722.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIiBzdHJva2U9IndoaXRlIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMS41IiBjb2xvcj0id2hpdGUiPjxwYXRoIGQ9Ik02LjE3NSAxMC4zMzNjLTEuMjA4LjQwOC0xLjk1NS45Ny0xLjk1NSAxLjU5M2MwIC44NDggMS4zODkgMS41ODcgMy40NCAxLjk3bTAgMGMtLjc2Mi4zODYtMS4yMTcuODc1LTEuMjE3IDEuNDA4YzAgMS4yNDMgMi40ODcgMi4yNTEgNS41NTUgMi4yNTFhMTMgMTMgMCAwIDAgMi4yMjMtLjE4N20tNi41Ni0zLjQ3MWExOCAxOCAwIDAgMCAzLjIyNi4yOGMxLjcwOCAwIDMuMjY1LS4yMTYgNC40NDUtLjU3M20xLjExLTMuNDhjLTEuNDExLjQxNy0zLjM3OS42NzYtNS41NTUuNjc2Yy00LjI5NSAwLTcuNzc4LTEuMDA4LTcuNzc4LTIuMjUyYzAtLjk2IDIuMDc3LTEuNzggNS0yLjEwNCIvPjxwYXRoIGQ9Ik0yMiAxOS4wN0MyMiAyMC42ODggMTcuNTIzIDIyIDEyIDIyUzIgMjAuNjg4IDIgMTkuMDdjMC0xLjE1IDEuNzA3LTIuMTQ2IDUtMi42MjZtMTEuNzYtNy42NTZjNC4yMTQtMS4wOTQgNC44MTYgNS40NjgtMS4yMDUgNy42NTZNMTcuNTU4IDJjLS43NC4xMjMtMi4xMzMuODE1LTEuNzc4IDIuNTkzYy4zNTYgMS43NzctLjE0OCAyLjcxNi0uNDQ0IDIuOTYzTTEzLjExMyAyYy0uNzQxLjE0OC0yLjEzNC45NzgtMS43NzggMy4xMTFzLS4xNDggMi43MDQtLjQ0NCAzIi8+PC9nPjwvc3ZnPg==)](https://www.oracle.com/cn/java/)
&nbsp;
[![IntelliJ IDEA](https://img.shields.io/badge/Jetbrains-IntelliJ%20IDEA-%2311AB00.svg?style=Plastic&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/?from=ZhiYouToolkit)

</div>

<br/>

## 功能 🎉
#### 代码生成与辅助
- [Json转JavaBean](https://zhiyou.memoryzy.cn/json-to-javabean.html)：Json 反序列化为 JavaBean，支持嵌套 Array、Object 属性。
- [JavaBean转Json](https://zhiyou.memoryzy.cn/javabean-to-json.html)：JavaBean 序列化为 Json，支持嵌套 Object 属性，支持 FastJson、Jackson 注解。
- [ResultMap标签生成](https://zhiyou.memoryzy.cn/generates-resultmap-tag.html)：通过 SQL 与 Java 类属性映射生成 MyBatis-ResultMap 标签。
- [Where标签生成](https://zhiyou.memoryzy.cn/generates-where-tag.html)：通过 Java 类属性生成 MyBatis-Where 条件标签。
- [类属性映射与方法列举](https://zhiyou.memoryzy.cn/attributes-mapping.html)：类与类之间属性匹配，单独类的列举 Getter、Setter 方法等功能。
- [JDK序列化ID生成](https://zhiyou.memoryzy.cn/generate-serialization-id.html)：为 Java 类生成 JDK序列化ID，并实现序列化接口。
- [MyBatis-Mapper文件生成](https://zhiyou.memoryzy.cn/generate-mybatis-files.html)：生成 Mapper 接口、Mapper-Xml、映射类等文件，并自动匹配文件路径。
- [指定注解、注释生成](https://zhiyou.memoryzy.cn/generate-annotation.html)：为类属性生成指定注解及注释。

#### 代码与文本格式转换
- [单行注释转Java文档注释](https://zhiyou.memoryzy.cn/convert-to-javadoc.html)
- [字符串格式转换](https://zhiyou.memoryzy.cn/convert-strings.html)：包含驼峰、下划线互转，中文转拼音功能，支持多行多文本转换。
- [时间戳与时间转换](https://zhiyou.memoryzy.cn/convert-timestamp-to-date.html)：快速转换时间戳 / 时间。
- [JSON/XML转换](https://zhiyou.memoryzy.cn/convert-json-to-xml.html)：快速转换 Json / XML。

#### 拓展功能
- [代码笔记功能](https://zhiyou.memoryzy.cn/code-note.html)：记录代码片段、笔记、备忘，支持标签、导入、导出等功能。
- [文本互相比对](https://zhiyou.memoryzy.cn/text-comparison.html)：精准辨识文本的不同之处。
- [Json窗口及Json处理](https://zhiyou.memoryzy.cn/json-panel.html)：支持 Json格式化、Json压缩、Json结构化。
- [Jasypt加解密](https://zhiyou.memoryzy.cn/jasypt-encrypt-and-decrypt.html)：Jasypt 加密、解密功能，支持对称加密、非对称加密。
- [Spring接口路径提取](https://zhiyou.memoryzy.cn/copy-restful-path.html)：快速复制 Spring 接口路径。

<br/>

## 快速开始 🚀
> 导航：[使用文档](https://zhiyou.memoryzy.cn/overview.html)

<br/>

## 安装
> - 通过 IDE 内置插件系统搜索 `ZhiYouToolkit` 并安装。
> - 通过 **[JetBrains Marketplace](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)** 或 **[Github Releases](https://github.com/MemoryZy/ZhiYouToolkit/releases)** 下载插件并手动安装。

<br/>

## 提示
> 对于 IntelliJ IDEA 来说，插件仅支持 IntelliJ IDEA 2022.2 及以上版本。

<br/>

## 反馈
> 使用过程中发现任何问题都可以提 [Issue](https://github.com/MemoryZy/ZhiYouToolkit/issues) 和 [PR](https://github.com/MemoryZy/ZhiYouToolkit/pulls)，并且有任何想法也可以直接提交至 [Discussions](https://github.com/MemoryZy/ZhiYouToolkit/discussions/categories/ideas)，感谢支持。

<br/>

## 开源许可证
[![Licence](https://img.shields.io/github/license/MemoryZy/ZhiYouToolkit?style=for-the-badge)](./LICENSE)

<br/>

## 插件截图

![Json Panel](https://web-images-5we.pages.dev/images/JsonPanel.jpg)

![MyBatis](https://web-images-5we.pages.dev/images/MyBatis.jpg)

![Mapping](https://web-images-5we.pages.dev/images/Mapping.gif)


