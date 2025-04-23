# 图片转 Minecraft 皮肤工具

|  输入项   |       含义        |
|:------:|:---------------:|
| `字面量`  |    按原样输入的内容。    |
| `<参数>` | 需使用一合适的值来替换的参数。 |

|     修饰符      |       含义        |
|:------------:|:---------------:|
|   `[输入项]`    |    该输入项是可选的。    |
| `(输入项\|输入项)` | 必选，选择其中一个输入项填写。 |
| `[输入项\|输入项]` | 可选，选择其中一个输入项填写。 |

## 参数

<table>
    <tr>
        <th> 参数 </th>
        <th> 描述 </th>
    </tr>
    <tr>
        <td> <code>-i</code>, <code>--input &lt;Path&gt; [&lt;Face&gt;] [&lt;FitMode&gt;]</code> </td>
        <td style="text-align: center;" rowspan="3"> 定义源图片路径，转换后的面，和适应模式。 </td>
    </tr>
    <tr>
        <td> <code>-i</code>, <code>--input &lt;Path&gt; [&lt;Face&gt; [&lt;Face&gt; ...]] [&lt;FitMode&gt;]</code> </td>
    </tr>
    <tr>
        <td> <code>-f</code>, <code>--face &lt;Face&gt; &lt;Path&gt; [&lt;FitMode&gt;]</code> </td>
    </tr>
    <tr>
        <td> <code>-o</code>, <code>--output &lt;Path&gt;</code> </td>
        <td style="text-align: center;"> 定义导出的皮肤文件路径。 <br> 可仅给定导出文件夹。 </td>
    </tr>
    <tr>
        <td> <code>-r</code>, <code>--resolution &lt;int&gt;</code> </td>
        <td style="text-align: center;"> 定义皮肤分辨率（默认&nbsp;64）。 </td>
    </tr>
    <tr>
        <td> <code>-m</code>, <code>--model &lt;SkinModel&gt;</code> </td>
        <td style="text-align: center;"> 定义皮肤模型。 </td>
    </tr>
    <tr>
        <td> <code>-b</code>, <code>--background &lt;RGB&gt;</code> </td>
        <td style="text-align: center;"> 定义背景颜色（默认&nbsp;000000）。 </td>
    </tr>
    <tr>
        <td> <code>-g</code>, <code>--gradient &lt;boolean&gt;</code> </td>
        <td style="text-align: center;"> 定义是否在皮肤侧边添加渐变色。 </td>
    </tr>
</table>

### 类型

<table>
    <tr>
        <th> 类型 </th>
        <th> 可选值 </th>
        <th> 描述 </th>
    </tr>
    <tr>
        <td style="text-align: center;" rowspan="4"> <code>&lt;Face&gt;</code> </td>
        <td style="text-align: center;"> <code>f</code> </td>
        <td style="text-align: center;"> 皮肤前部 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>b</code> </td>
        <td style="text-align: center;"> 皮肤背部 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>fo</code> </td>
        <td style="text-align: center;"> 皮肤外层前部 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>bo</code> </td>
        <td style="text-align: center;"> 皮肤外层背部 </td>
    </tr>
    <tr>
        <td style="text-align: center;" rowspan="2"> <code>&lt;FitMode&gt;</code> </td>
        <td style="text-align: center;"> <code>fill</code> </td>
        <td style="text-align: center;"> 拉伸以填充整面皮肤 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>cover</code> </td>
        <td style="text-align: center;"> 等比放大图片并覆盖整面皮肤 <br> 但可能会裁剪部分内容 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>&lt;Path&gt;</code> </td>
        <td style="text-align: center;"> 文件路径 </td>
        <td style="text-align: center;"> 例：<code>C:\path\to\the\file\</code> <code>directory/file.png</code> </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>&lt;RGB&gt;</code> </td>
        <td style="text-align: center;"> <code>000000</code> - <code>FFFFFF</code> </td>
        <td style="text-align: center;"> RGB，如包含&nbsp;Alpha&nbsp;值则丢弃 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>&lt;ARGB&gt;</code> </td>
        <td style="text-align: center;"> <code>00000000</code> - <code>FFFFFFFF</code> </td>
        <td style="text-align: center;"> ARGB </td>
    </tr>
    <tr>
        <td style="text-align: center;" rowspan="5"> <code>&lt;SkinModel&gt;</code> </td>
        <td style="text-align: center;"> <code>steve</code> </td>
        <td style="text-align: center;" rowspan="3"> 粗胳膊 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>classic</code> </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>wide</code> </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>alex</code> </td>
        <td style="text-align: center;" rowspan="2"> 细胳膊 </td>
    </tr>
    <tr>
        <td style="text-align: center;"> <code>slim</code> </td>
    </tr>
</table>
