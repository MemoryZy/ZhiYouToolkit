<div align="center">

# [![ZhiYouToolkit](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/src/main/resources/META-INF/pluginIcon@30x30.svg)](https://github.com/MemoryZy/ZhiYouToolkit) **ZhiYouToolkit**

English / [简体中文](./README_zh.md)

ZhiYouToolkit is an IDEA plugin designed to solve repetitive tasks and pain points in daily development, enhancing code development efficiency and quality.

知游工具，一款用于解决日常开发中大量的重复工作、痛点，提高代码开发效率和质量的IDEA插件。


[![Homepage](https://img.shields.io/badge/Homepage-ZhiYouToolkit-0db7ed.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTEwIDE5di01aDR2NWMwIC41NS40NSAxIDEgMWgzYy41NSAwIDEtLjQ1IDEtMXYtN2gxLjdjLjQ2IDAgLjY4LS41Ny4zMy0uODdMMTIuNjcgMy42Yy0uMzgtLjM0LS45Ni0uMzQtMS4zNCAwbC04LjM2IDcuNTNjLS4zNC4zLS4xMy44Ny4zMy44N0g1djdjMCAuNTUuNDUgMSAxIDFoM2MuNTUgMCAxLS40NSAxLTEiLz48L3N2Zz4=)](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)
&nbsp;
[![Release](https://img.shields.io/github/v/release/MemoryZy/ZhiYouToolkit?style=Plastic&label=Release&color=d05ce3&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIj48cGF0aCBkPSJNMjQgMHYyNEgwVjB6TTEyLjU5MyAyMy4yNThsLS4wMTEuMDAybC0uMDcxLjAzNWwtLjAyLjAwNGwtLjAxNC0uMDA0bC0uMDcxLS4wMzVjLS4wMS0uMDA0LS4wMTktLjAwMS0uMDI0LjAwNWwtLjAwNC4wMWwtLjAxNy40MjhsLjAwNS4wMmwuMDEuMDEzbC4xMDQuMDc0bC4wMTUuMDA0bC4wMTItLjAwNGwuMTA0LS4wNzRsLjAxMi0uMDE2bC4wMDQtLjAxN2wtLjAxNy0uNDI3Yy0uMDAyLS4wMS0uMDA5LS4wMTctLjAxNy0uMDE4bS4yNjUtLjExM2wtLjAxMy4wMDJsLS4xODUuMDkzbC0uMDEuMDFsLS4wMDMuMDExbC4wMTguNDNsLjAwNS4wMTJsLjAwOC4wMDdsLjIwMS4wOTNjLjAxMi4wMDQuMDIzIDAgLjAyOS0uMDA4bC4wMDQtLjAxNGwtLjAzNC0uNjE0Yy0uMDAzLS4wMTItLjAxLS4wMi0uMDItLjAyMm0tLjcxNS4wMDJhLjAyMy4wMjMgMCAwIDAtLjAyNy4wMDZsLS4wMDYuMDE0bC0uMDM0LjYxNGMwIC4wMTIuMDA3LjAyLjAxNy4wMjRsLjAxNS0uMDAybC4yMDEtLjA5M2wuMDEtLjAwOGwuMDA0LS4wMTFsLjAxNy0uNDNsLS4wMDMtLjAxMmwtLjAxLS4wMXoiLz48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTIwLjI0NSAxNC43NWMuOTM1LjYxNC44OTIgMi4wMzctLjEyOSAyLjU3NmwtNy4xODEgMy43OTZhMiAyIDAgMCAxLTEuODcgMGwtNy4xODEtMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS44NyAwbDcuMTgxLTMuNzk2YTEuNTkgMS41OSAwIDAgMCAuMTMtLjA3NlptMC00YTEuNSAxLjUgMCAwIDEgMCAyLjUwMWwtLjEyOS4wNzVsLTcuMTgxIDMuNzk2YTIgMiAwIDAgMS0xLjcwNy4wNzdsLS4xNjItLjA3N2wtNy4xODItMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS43MDguMDc3bC4xNjItLjA3N2w3LjE4MS0zLjc5NmExLjU5IDEuNTkgMCAwIDAgLjEzLS4wNzZabS03LjMxLTcuODcybDcuMTgxIDMuNzk2YzEuMDY2LjU2MyAxLjA2NiAyLjA5IDAgMi42NTJsLTcuMTgxIDMuNzk3YTIgMiAwIDAgMS0xLjg3IDBMMy44ODQgOS4zMjZjLTEuMDY2LS41NjMtMS4wNjYtMi4wODkgMC0yLjY1Mmw3LjE4MS0zLjc5NmEyIDIgMCAwIDEgMS44NyAwIi8+PC9nPjwvc3ZnPg==)](https://github.com/MemoryZy/ZhiYouToolkit/releases)
&nbsp;
[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/MemoryZy/ZhiYouToolkit/Compatibility.yml?branch=main&style=plastic&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0ibTE3LjE1IDIwLjdsLTYuMDUtNi4xcS0uNS4yLTEuMDEyLjNUOSAxNXEtMi41IDAtNC4yNS0xLjc1VDMgOXEwLS45LjI1LTEuNzEydC43LTEuNTM4TDcuNiA5LjRsMS44LTEuOGwtMy42NS0zLjY1cS43MjUtLjQ1IDEuNTM4LS43VDkgM3EyLjUgMCA0LjI1IDEuNzVUMTUgOXEwIC41NzUtLjEgMS4wODh0LS4zIDEuMDEybDYuMSA2LjA1cS4zLjMuMy43MjV0LS4zLjcyNWwtMi4xIDIuMXEtLjMuMy0uNzI1LjN0LS43MjUtLjMiLz48L3N2Zz4%3D&label=Build)](https://github.com/MemoryZy/ZhiYouToolkit/actions/workflows/Compatibility.yml)
&nbsp;
[![Java](https://img.shields.io/badge/Lang-Java-ff5722.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIiBzdHJva2U9IndoaXRlIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMS41IiBjb2xvcj0id2hpdGUiPjxwYXRoIGQ9Ik02LjE3NSAxMC4zMzNjLTEuMjA4LjQwOC0xLjk1NS45Ny0xLjk1NSAxLjU5M2MwIC44NDggMS4zODkgMS41ODcgMy40NCAxLjk3bTAgMGMtLjc2Mi4zODYtMS4yMTcuODc1LTEuMjE3IDEuNDA4YzAgMS4yNDMgMi40ODcgMi4yNTEgNS41NTUgMi4yNTFhMTMgMTMgMCAwIDAgMi4yMjMtLjE4N20tNi41Ni0zLjQ3MWExOCAxOCAwIDAgMCAzLjIyNi4yOGMxLjcwOCAwIDMuMjY1LS4yMTYgNC40NDUtLjU3M20xLjExLTMuNDhjLTEuNDExLjQxNy0zLjM3OS42NzYtNS41NTUuNjc2Yy00LjI5NSAwLTcuNzc4LTEuMDA4LTcuNzc4LTIuMjUyYzAtLjk2IDIuMDc3LTEuNzggNS0yLjEwNCIvPjxwYXRoIGQ9Ik0yMiAxOS4wN0MyMiAyMC42ODggMTcuNTIzIDIyIDEyIDIyUzIgMjAuNjg4IDIgMTkuMDdjMC0xLjE1IDEuNzA3LTIuMTQ2IDUtMi42MjZtMTEuNzYtNy42NTZjNC4yMTQtMS4wOTQgNC44MTYgNS40NjgtMS4yMDUgNy42NTZNMTcuNTU4IDJjLS43NC4xMjMtMi4xMzMuODE1LTEuNzc4IDIuNTkzYy4zNTYgMS43NzctLjE0OCAyLjcxNi0uNDQ0IDIuOTYzTTEzLjExMyAyYy0uNzQxLjE0OC0yLjEzNC45NzgtMS43NzggMy4xMTFzLS4xNDggMi43MDQtLjQ0NCAzIi8+PC9nPjwvc3ZnPg==)](https://www.oracle.com/cn/java/)
&nbsp;
[![IntelliJ IDEA](https://img.shields.io/badge/Jetbrains-IntelliJ%20IDEA-%2311AB00.svg?style=Plastic&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/?from=ZhiYouToolkit)

<br/>

</div>


## Features 🎉
- [Code Notes](https://zhiyou.memoryzy.cn/code-note.html): Code Logging, Note Import, Note Export, Note Tagging.
- [Convert JSON to JavaBean](https://zhiyou.memoryzy.cn/json-to-javabean.html): Support for Nested Properties.
- [Convert JavaBean to JSON](https://zhiyou.memoryzy.cn/javabean-to-json.html): Support for Nested Properties, FastJson, and Jackson Annotations.
- [JSON Window and JSON Processing](https://zhiyou.memoryzy.cn/json-panel.html): JSON Formatting, JSON Compression, JSON Structuring.
- [String Format Conversion](https://zhiyou.memoryzy.cn/convert-strings.html): Convert Between Camel Case and Underscore, Convert Chinese to Pinyin.
- [Convert Single-line Comments to Documentation Comments](https://zhiyou.memoryzy.cn/convert-to-javadoc.html)
- [Generate ResultMap Tags](https://zhiyou.memoryzy.cn/generates-resultmap-tag.html): One-Click Generation of MyBatis ResultMap Tags.
- [Generate Where Tags](https://zhiyou.memoryzy.cn/generates-where-tag.html): One-Click Generation of MyBatis Where Tags.
- [Class Property Mapping and Method Enumeration](https://zhiyou.memoryzy.cn/attributes-mapping.html): Enumerate Getter and Setter Methods, Class Property Mapping Functionality.
- [Generate JDK Serialization ID](https://zhiyou.memoryzy.cn/generate-serialization-id.html): One-Click Generation of JDK Serialization ID.
- [Extract Spring Interface Paths](https://zhiyou.memoryzy.cn/copy-restful-path.html)
- [Convert Between Timestamps and Dates](https://zhiyou.memoryzy.cn/convert-timestamp-to-date.html)
- [Generate MyBatis-related Files](https://zhiyou.memoryzy.cn/generate-mybatis-files.html): Generate Mapper Interface, Mapper XML, Mapping Class, and Other Files.
- [Generate Specified Annotations and Comments](https://zhiyou.memoryzy.cn/generate-annotation.html): Generate Specified Annotations and Comments for Class Properties.
- [Jasypt Encryption and Decryption](https://zhiyou.memoryzy.cn/jasypt-encrypt-and-decrypt.html): Jasypt Encryption and Decryption Functionality, Support for Symmetric and Asymmetric Encryption.
- [Convert Between JSON and XML](https://zhiyou.memoryzy.cn/convert-json-to-xml.html)
- [Text Comparison](https://zhiyou.memoryzy.cn/text-comparison.html): Compare Differences Between Two Texts.
- ......
        
<br/>

## Get Started 🚀
> Navigation：**[Documentation](https://zhiyou.memoryzy.cn/overview.html)**

<br/>

## Installed
> - Search for `ZhiYouToolkit` in the IDE built-in plug-in system and install it.
> - Download the plugin via the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit) or [Github Releases](https://github.com/MemoryZy/ZhiYouToolkit/releases) and install it manually.

<br/>

## Tip
> For IntelliJ IDEA, the plugin only supports IntelliJ IDEA 2022.2 and above.

<br/>

## Feedback
> You can raise [issues](https://github.com/MemoryZy/ZhiYouToolkit/issues) and [PR](https://github.com/MemoryZy/ZhiYouToolkit/pulls) for any problems found during use, and any ideas can be directly submitted to [Discussions](https://github.com/MemoryZy/ZhiYouToolkit/discussions/categories/ideas), thanks for your support.

<br/>

## License
[![Licence](https://img.shields.io/github/license/MemoryZy/ZhiYouToolkit?style=for-the-badge)](./LICENSE)

<br/>

## Plugin Screenshots

![Json Panel](https://home.memoryzy.cn/zhiyou/JsonPanel.jpg)

![MyBatis](https://home.memoryzy.cn/zhiyou/MyBatis.jpg)

![Mapping](https://home.memoryzy.cn/zhiyou/Mapping.gif)



