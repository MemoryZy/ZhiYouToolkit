<div align="center">

# [![ZhiYouToolkit](https://cdn.jsdelivr.net/gh/MemoryZy/ZhiYouToolkit/src/main/resources/META-INF/pluginIcon@30x30.svg)](https://github.com/MemoryZy/ZhiYouToolkit) **ZhiYouToolkit**

English / [简体中文](./README_zh.md)

ZhiYouToolkit is an IDEA plugin designed to solve repetitive tasks and pain points in daily development, enhancing code development efficiency and quality.

知游工具，一款用于解决日常开发中大量的重复工作、痛点，提高代码开发效率和质量的IDEA插件。


[![Plugin Homepage](https://img.shields.io/badge/Homepage-ZhiYouToolkit-0db7ed.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTEwIDE5di01aDR2NWMwIC41NS40NSAxIDEgMWgzYy41NSAwIDEtLjQ1IDEtMXYtN2gxLjdjLjQ2IDAgLjY4LS41Ny4zMy0uODdMMTIuNjcgMy42Yy0uMzgtLjM0LS45Ni0uMzQtMS4zNCAwbC04LjM2IDcuNTNjLS4zNC4zLS4xMy44Ny4zMy44N0g1djdjMCAuNTUuNDUgMSAxIDFoM2MuNTUgMCAxLS40NSAxLTEiLz48L3N2Zz4=)](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)
&nbsp;
[![Release](https://img.shields.io/badge/Release-v1.3.1-d05ce3.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIj48cGF0aCBkPSJNMjQgMHYyNEgwVjB6TTEyLjU5MyAyMy4yNThsLS4wMTEuMDAybC0uMDcxLjAzNWwtLjAyLjAwNGwtLjAxNC0uMDA0bC0uMDcxLS4wMzVjLS4wMS0uMDA0LS4wMTktLjAwMS0uMDI0LjAwNWwtLjAwNC4wMWwtLjAxNy40MjhsLjAwNS4wMmwuMDEuMDEzbC4xMDQuMDc0bC4wMTUuMDA0bC4wMTItLjAwNGwuMTA0LS4wNzRsLjAxMi0uMDE2bC4wMDQtLjAxN2wtLjAxNy0uNDI3Yy0uMDAyLS4wMS0uMDA5LS4wMTctLjAxNy0uMDE4bS4yNjUtLjExM2wtLjAxMy4wMDJsLS4xODUuMDkzbC0uMDEuMDFsLS4wMDMuMDExbC4wMTguNDNsLjAwNS4wMTJsLjAwOC4wMDdsLjIwMS4wOTNjLjAxMi4wMDQuMDIzIDAgLjAyOS0uMDA4bC4wMDQtLjAxNGwtLjAzNC0uNjE0Yy0uMDAzLS4wMTItLjAxLS4wMi0uMDItLjAyMm0tLjcxNS4wMDJhLjAyMy4wMjMgMCAwIDAtLjAyNy4wMDZsLS4wMDYuMDE0bC0uMDM0LjYxNGMwIC4wMTIuMDA3LjAyLjAxNy4wMjRsLjAxNS0uMDAybC4yMDEtLjA5M2wuMDEtLjAwOGwuMDA0LS4wMTFsLjAxNy0uNDNsLS4wMDMtLjAxMmwtLjAxLS4wMXoiLz48cGF0aCBmaWxsPSJ3aGl0ZSIgZD0iTTIwLjI0NSAxNC43NWMuOTM1LjYxNC44OTIgMi4wMzctLjEyOSAyLjU3NmwtNy4xODEgMy43OTZhMiAyIDAgMCAxLTEuODcgMGwtNy4xODEtMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS44NyAwbDcuMTgxLTMuNzk2YTEuNTkgMS41OSAwIDAgMCAuMTMtLjA3NlptMC00YTEuNSAxLjUgMCAwIDEgMCAyLjUwMWwtLjEyOS4wNzVsLTcuMTgxIDMuNzk2YTIgMiAwIDAgMS0xLjcwNy4wNzdsLS4xNjItLjA3N2wtNy4xODItMy43OTZjLTEuMDItLjU0LTEuMDY0LTEuOTYyLS4xMjktMi41NzZsLjA2My4wNGw3LjI0NyAzLjgzMmEyIDIgMCAwIDAgMS43MDguMDc3bC4xNjItLjA3N2w3LjE4MS0zLjc5NmExLjU5IDEuNTkgMCAwIDAgLjEzLS4wNzZabS03LjMxLTcuODcybDcuMTgxIDMuNzk2YzEuMDY2LjU2MyAxLjA2NiAyLjA5IDAgMi42NTJsLTcuMTgxIDMuNzk3YTIgMiAwIDAgMS0xLjg3IDBMMy44ODQgOS4zMjZjLTEuMDY2LS41NjMtMS4wNjYtMi4wODkgMC0yLjY1Mmw3LjE4MS0zLjc5NmEyIDIgMCAwIDEgMS44NyAwIi8+PC9nPjwvc3ZnPg==)](https://github.com/MemoryZy/ZhiYouToolkit/releases)
&nbsp;
[![Java](https://img.shields.io/badge/Lang-Java-ff5722.svg?style=Plastic&logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDI0IDI0Ij48ZyBmaWxsPSJub25lIiBzdHJva2U9IndoaXRlIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMS41IiBjb2xvcj0id2hpdGUiPjxwYXRoIGQ9Ik02LjE3NSAxMC4zMzNjLTEuMjA4LjQwOC0xLjk1NS45Ny0xLjk1NSAxLjU5M2MwIC44NDggMS4zODkgMS41ODcgMy40NCAxLjk3bTAgMGMtLjc2Mi4zODYtMS4yMTcuODc1LTEuMjE3IDEuNDA4YzAgMS4yNDMgMi40ODcgMi4yNTEgNS41NTUgMi4yNTFhMTMgMTMgMCAwIDAgMi4yMjMtLjE4N20tNi41Ni0zLjQ3MWExOCAxOCAwIDAgMCAzLjIyNi4yOGMxLjcwOCAwIDMuMjY1LS4yMTYgNC40NDUtLjU3M20xLjExLTMuNDhjLTEuNDExLjQxNy0zLjM3OS42NzYtNS41NTUuNjc2Yy00LjI5NSAwLTcuNzc4LTEuMDA4LTcuNzc4LTIuMjUyYzAtLjk2IDIuMDc3LTEuNzggNS0yLjEwNCIvPjxwYXRoIGQ9Ik0yMiAxOS4wN0MyMiAyMC42ODggMTcuNTIzIDIyIDEyIDIyUzIgMjAuNjg4IDIgMTkuMDdjMC0xLjE1IDEuNzA3LTIuMTQ2IDUtMi42MjZtMTEuNzYtNy42NTZjNC4yMTQtMS4wOTQgNC44MTYgNS40NjgtMS4yMDUgNy42NTZNMTcuNTU4IDJjLS43NC4xMjMtMi4xMzMuODE1LTEuNzc4IDIuNTkzYy4zNTYgMS43NzctLjE0OCAyLjcxNi0uNDQ0IDIuOTYzTTEzLjExMyAyYy0uNzQxLjE0OC0yLjEzNC45NzgtMS43NzggMy4xMTFzLS4xNDggMi43MDQtLjQ0NCAzIi8+PC9nPjwvc3ZnPg==)](https://www.oracle.com/cn/java/)
&nbsp;
[![IntelliJ IDEA](https://img.shields.io/badge/Jetbrains-IntelliJ%20IDEA-%2311AB00.svg?style=Plastic&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/?from=ZhiYouToolkit)

<br/>

</div>


## Features 🎉
- [Code Notes](https://home.memoryzy.cn/zhiyou/codenote/): Code Logging, Note Import, Note Export, Note Tagging.
- [Convert JSON to JavaBean](https://home.memoryzy.cn/zhiyou/jsontobean/): Support for Nested Properties.
- [Convert JavaBean to JSON](https://home.memoryzy.cn/zhiyou/beantojson/): Support for Nested Properties, FastJson, and Jackson Annotations.
- [JSON Window and JSON Processing](https://home.memoryzy.cn/zhiyou/json/panel/): JSON Formatting, JSON Compression, JSON Structuring.
- [String Format Conversion](https://home.memoryzy.cn/zhiyou/format/conversion/): Convert Between Camel Case and Underscore, Convert Chinese to Pinyin.
- [Convert Single-line Comments to Documentation Comments](https://home.memoryzy.cn/zhiyou/doc/comment/)
- [Generate ResultMap Tags](https://home.memoryzy.cn/zhiyou/mybatis/resultmap/): One-Click Generation of MyBatis ResultMap Tags.
- [Generate Where Tags](https://home.memoryzy.cn/zhiyou/mybatis/where/): One-Click Generation of MyBatis Where Tags.
- [Class Property Mapping and Method Enumeration](https://home.memoryzy.cn/zhiyou/property/mapping/): Enumerate Getter and Setter Methods, Class Property Mapping Functionality.
- [Generate JDK Serialization ID](https://home.memoryzy.cn/zhiyou/generate/serialversionid/): One-Click Generation of JDK Serialization ID.
- [Extract Spring Interface Paths](https://home.memoryzy.cn/zhiyou/get/path/)
- [Convert Between Timestamps and Dates](https://home.memoryzy.cn/zhiyou/timestamp/)
- [Generate MyBatis-related Files](https://home.memoryzy.cn/zhiyou/mybatis/generate/): Generate Mapper Interface, Mapper XML, Mapping Class, and Other Files.
- [Generate Specified Annotations and Comments](https://home.memoryzy.cn/zhiyou/create/annotation/): Generate Specified Annotations and Comments for Class Properties.
- [Jasypt Encryption and Decryption](https://home.memoryzy.cn/zhiyou/jasypt/): Jasypt Encryption and Decryption Functionality, Support for Symmetric and Asymmetric Encryption.
- [Convert Between JSON and XML](https://home.memoryzy.cn/zhiyou/jsontoxml/)
- [Text Comparison](https://home.memoryzy.cn/zhiyou/textcompare/): Compare Differences Between Two Texts.
- ......
        
<br/>

## Get Started 🚀
> Navigation：**[Introduction and usage documentation](https://home.memoryzy.cn/zhiyou/)**

<br/>

## Installed
> \> &nbsp;Install via **[Jetbrains Marketplace](https://plugins.jetbrains.com/plugin/24381-zhiyoutoolkit)**.  
> \> &nbsp;Download the plugin on **[Github Release](https://github.com/MemoryZy/ZhiYouToolkit/releases)** and install it manually.

<br/>

## Tip
> IDEA Ultimate 2022.2 or later is supported.

<br/>

## License
[![Licence](https://img.shields.io/badge/Licence-Apache%202.0-97ca00.svg?style=for-the-badge&logoColor=white)](./LICENSE)
