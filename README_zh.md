<div align="center">

# [![ZhiYouToolkit](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/src/main/resources/META-INF/pluginIcon@30x30.svg)](https://github.com/MemoryZy/ZhiYouToolkit) **ZhiYouToolkit**

[English](./README.md) / 简体中文

知游工具，一款用于解决日常开发中大量的重复工作、痛点，提高代码开发效率和质量的IDEA插件。

[![Plugin Homepage](https://img.shields.io/badge/Homepage-ZhiYouToolkit-0db7ed.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTEwIDE5di01aDR2NWMwIC41NS40NSAxIDEgMWgzYy41NSAwIDEtLjQ1IDEtMXYtN2gxLjdjLjQ2IDAgLjY4LS41Ny4zMy0uODdMMTIuNjcgMy42Yy0uMzgtLjM0LS45Ni0uMzQtMS4zNCAwbC04LjM2IDcuNTNjLS4zNC4zLS4xMy44Ny4zMy44N0g1djdjMCAuNTUuNDUgMSAxIDFoM2MuNTUgMCAxLS40NSAxLTEiLz48L3N2Zz4=)](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)
&nbsp;
[![Release](https://img.shields.io/github/v/release/MemoryZy/ZhiYouToolkit?style=Plastic&label=Release&color=d05ce3&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIj48cGF0aCBkPSJNMjQgMHYyNEgwVjB6TTEyLjU5MyAyMy4yNThsLS4wMTEuMDAybC0uMDcxLjAzNWwtLjAyLjAwNGwtLjAxNC0uMDA0bC0uMDcxLS4wMzVjLS4wMS0uMDA0LS4wMTktLjAwMS0uMDI0LjAwNWwtLjAwNC4wMWwtLjAxNy40MjhsLjAwNS4wMmwuMDEuMDEzbC4xMDQuMDc0bC4wMTUuMDA0bC4wMTItLjAwNGwuMTA0LS4wNzRsLjAxMi0uMDE2bC4wMDQtLjAxN2wtLjAxNy0uNDI3Yy0uMDAyLS4wMS0uMDA5LS4wMTctLjAxNy0uMDE4bS4yNjUtLjExM2wtLjAxMy4wMDJsLS4xODUuMDkzbC0uMDEuMDFsLS4wMDMuMDExbC4wMTguNDNsLjAwNS4wMTJsLjAwOC4wMDdsLjIwMS4wOTNjLjAxMi4wMDQuMDIzIDAgLjAyOS0uMDA4bC4wMDQtLjAxNGwtLjAzNC0uNjE0Yy0uMDAzLS4wMTItLjAxLS4wMi0uMDItLjAyMm0tLjcxNS4wMDJhLjAyMy4wMjMgMCAwIDAtLjAyNy4wMDZsLS4wMDYuMDE0bC0uMDM0LjYxNGMwIC4wMTIuMDA3LjAyLjAxNy4wMjRsLjAxNS0uMDAybC4yMDEtLjA5M2wuMDEtLjAwOGwuMDA0LS4wMTFsLjAxNy0uNDNsLS4wMDMtLjAxMmwtLjAxLS4wMXoiLz48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTIwLjI0NSAxNC43NWMuOTM1LjYxNC44OTIgMi4wMzctLjEyOSAyLjU3NmwtNy4xODEgMy43OTZhMiAyIDAgMCAxLTEuODcgMGwtNy4xODEtMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS44NyAwbDcuMTgxLTMuNzk2YTEuNTkgMS41OSAwIDAgMCAuMTMtLjA3NlptMC00YTEuNSAxLjUgMCAwIDEgMCAyLjUwMWwtLjEyOS4wNzVsLTcuMTgxIDMuNzk2YTIgMiAwIDAgMS0xLjcwNy4wNzdsLS4xNjItLjA3N2wtNy4xODItMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS43MDguMDc3bC4xNjItLjA3N2w3LjE4MS0zLjc5NmExLjU5IDEuNTkgMCAwIDAgLjEzLS4wNzZabS03LjMxLTcuODcybDcuMTgxIDMuNzk2YzEuMDY2LjU2MyAxLjA2NiAyLjA5IDAgMi42NTJsLTcuMTgxIDMuNzk3YTIgMiAwIDAgMS0xLjg3IDBMMy44ODQgOS4zMjZjLTEuMDY2LS41NjMtMS4wNjYtMi4wODkgMC0yLjY1Mmw3LjE4MS0zLjc5NmEyIDIgMCAwIDEgMS44NyAwIi8+PC9nPjwvc3ZnPg==)](https://github.com/MemoryZy/ZhiYouToolkit/releases)
&nbsp;
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/MemoryZy/ZhiYouToolkit/Compatibility.yml?branch=main&style=plastic&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0ibTE3LjE1IDIwLjdsLTYuMDUtNi4xcS0uNS4yLTEuMDEyLjNUOSAxNXEtMi41IDAtNC4yNS0xLjc1VDMgOXEwLS45LjI1LTEuNzEydC43LTEuNTM4TDcuNiA5LjRsMS44LTEuOGwtMy42NS0zLjY1cS43MjUtLjQ1IDEuNTM4LS43VDkgM3EyLjUgMCA0LjI1IDEuNzVUMTUgOXEwIC41NzUtLjEgMS4wODh0LS4zIDEuMDEybDYuMSA2LjA1cS4zLjMuMy43MjV0LS4zLjcyNWwtMi4xIDIuMXEtLjMuMy0uNzI1LjN0LS43MjUtLjMiLz48L3N2Zz4%3D&label=Build)](https://github.com/MemoryZy/ZhiYouToolkit/actions/workflows/Compatibility.yml)
&nbsp;
[![Java](https://img.shields.io/badge/Lang-Java-ff5722.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIiBzdHJva2U9IndoaXRlIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMS41IiBjb2xvcj0id2hpdGUiPjxwYXRoIGQ9Ik02LjE3NSAxMC4zMzNjLTEuMjA4LjQwOC0xLjk1NS45Ny0xLjk1NSAxLjU5M2MwIC44NDggMS4zODkgMS41ODcgMy40NCAxLjk3bTAgMGMtLjc2Mi4zODYtMS4yMTcuODc1LTEuMjE3IDEuNDA4YzAgMS4yNDMgMi40ODcgMi4yNTEgNS41NTUgMi4yNTFhMTMgMTMgMCAwIDAgMi4yMjMtLjE4N20tNi41Ni0zLjQ3MWExOCAxOCAwIDAgMCAzLjIyNi4yOGMxLjcwOCAwIDMuMjY1LS4yMTYgNC40NDUtLjU3M20xLjExLTMuNDhjLTEuNDExLjQxNy0zLjM3OS42NzYtNS41NTUuNjc2Yy00LjI5NSAwLTcuNzc4LTEuMDA4LTcuNzc4LTIuMjUyYzAtLjk2IDIuMDc3LTEuNzggNS0yLjEwNCIvPjxwYXRoIGQ9Ik0yMiAxOS4wN0MyMiAyMC42ODggMTcuNTIzIDIyIDEyIDIyUzIgMjAuNjg4IDIgMTkuMDdjMC0xLjE1IDEuNzA3LTIuMTQ2IDUtMi42MjZtMTEuNzYtNy42NTZjNC4yMTQtMS4wOTQgNC44MTYgNS40NjgtMS4yMDUgNy42NTZNMTcuNTU4IDJjLS43NC4xMjMtMi4xMzMuODE1LTEuNzc4IDIuNTkzYy4zNTYgMS43NzctLjE0OCAyLjcxNi0uNDQ0IDIuOTYzTTEzLjExMyAyYy0uNzQxLjE0OC0yLjEzNC45NzgtMS43NzggMy4xMTFzLS4xNDggMi43MDQtLjQ0NCAzIi8+PC9nPjwvc3ZnPg==)](https://www.oracle.com/cn/java/)
&nbsp;
[![IntelliJ IDEA](https://img.shields.io/badge/Jetbrains-IntelliJ%20IDEA-%2311AB00.svg?style=Plastic&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/?from=ZhiYouToolkit)

</div>

<br/>

## 功能 🎉
- [代码笔记](https://home.memoryzy.cn/zhiyou/codenote/)：记录代码、笔记导入、笔记导出、笔记标签。
- [Json转JavaBean](https://home.memoryzy.cn/zhiyou/jsontobean/)：支持嵌套属性。
- [JavaBean转Json](https://home.memoryzy.cn/zhiyou/beantojson/)：支持嵌套属性，支持FastJson、Jackson注解。
- [Json窗口及Json处理](https://home.memoryzy.cn/zhiyou/json/panel/)：Json格式化、Json压缩、Json结构化。
- [字符串格式转换](https://home.memoryzy.cn/zhiyou/format/conversion/)：驼峰与下划线互相转换、中文转拼音。
- [单行注释转文档注释](https://home.memoryzy.cn/zhiyou/doc/comment/)
- [ResultMap标签生成](https://home.memoryzy.cn/zhiyou/mybatis/resultmap/)：一键生成MyBatis-ResultMap标签。
- [Where标签生成](https://home.memoryzy.cn/zhiyou/mybatis/where/)：一键生成MyBatis-Where标签。
- [类属性映射与方法列举](https://home.memoryzy.cn/zhiyou/property/mapping/)：Getter、Setter方法列举、类属性映射功能。
- [JDK序列化ID生成](https://home.memoryzy.cn/zhiyou/generate/serialversionid/)：一键生成JDK序列化Id。
- [Spring接口路径提取](https://home.memoryzy.cn/zhiyou/get/path/)
- [时间戳与时间转换](https://home.memoryzy.cn/zhiyou/timestamp/)
- [MyBatis相关文件生成](https://home.memoryzy.cn/zhiyou/mybatis/generate/)：生成Mapper接口、Mapper-Xml、映射类等文件。
- [指定注解、注释生成](https://home.memoryzy.cn/zhiyou/create/annotation/)：为类属性生成指定注解及注释。
- [Jasypt加解密](https://home.memoryzy.cn/zhiyou/jasypt/)：Jasypt加密、解密功能，支持对称加密、非对称加密。
- [JSON/XML转换](https://home.memoryzy.cn/zhiyou/jsontoxml/)
- [文本互相比对](https://home.memoryzy.cn/zhiyou/textcompare/)：比对两份文本之间的不同之处。
- ......

<br/>

## 快速开始 🚀
> 导航：[插件介绍与使用文档](https://home.memoryzy.cn/zhiyou/)

<br/>

## 安装
> \> &nbsp;通过 **[Jetbrains插件市场](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)** 安装。  
> \> &nbsp;通过 **[Github Release](https://github.com/MemoryZy/ZhiYouToolkit/releases)** 下载插件并手动安装。

<br/>

## 插件截图

![Json Panel](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/images/JsonPanel.jpg)

![MyBatis](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/images/MyBatis.jpg)

![Mapping](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/images/Mapping.gif)

<br/>

## 提示
> 支持IDEA Ultimate 2022.2及以上版本。

<br/>

## 反馈
> 使用过程中发现任何问题都可以提 [Issue](https://github.com/MemoryZy/ZhiYouToolkit/issues) 和 [PR](https://github.com/MemoryZy/ZhiYouToolkit/pulls)，感谢支持。

<br/>

## 开源许可证
[![Licence](https://img.shields.io/github/license/MemoryZy/ZhiYouToolkit?style=for-the-badge)](./LICENSE)
