# Seam Carving 实验报告

来源：[Princeton 官方 Seam Carving 规格](https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php)。原始图片、SCUtility、展示客户端和 bundled algs4.jar 保留；实现 SeamCarver.java，分支 edidada/seam，独立 `.github/workflows/seam.yml`，JDK17 同时 Maven 多模块和 Gradle。发行包 Picture 只有 getRGB/setRGB，因此使用这些实际存在的接口。

用二维 RGB 数组保存当前图像，构造和 picture() 均做防御复制。内点能量为横/纵 RGB 中央差平方和的平方根，边界固定1000；查询 O(1)。找接缝把像素看作隐式分层 DAG，每一步只从上一层相邻三个像素转移，保存最优前驱并回溯；水平和垂直搜索共用坐标映射，不修改图像方向或状态。求解 O(WH) 时间、O(WH) 空间，不构造显式边对象。

删除前一次性验证 null、长度、索引、相邻差与图像最小尺寸，再按行或列复制保留下来的像素。删除为 O(WH)，不缓存旧能量，所以交替删行删列不会使用过期邻域。width/height 为 O(1)，其余方法不超过 O(WH)。依次删 r 行、c 列的粗略上界 O((r+c)WH)。

远程验收：5组 JUnit，官方3×4能量精确值、6×5示例，120张随机小图用 java.awt.Color 独立计算能量并完整枚举所有合法接缝核对最小代价；30张图混合删除直到1×1，每步检查全部保留像素、能量和最优接缝；非法参数不得改变图像，检查图像及接缝结果隔离。原始 PrintEnergy CLI 实际运行；原始 HJocean 图片删除100列30行后保存 PNG，作为真实图像处理产物。未运行可选GUI演示和私有评分。

编译、测试、图像处理均在 Actions 执行，最终结论以最终提交完成态 CI 为准。本地只编辑、检查文件和下载查看产物，不编译或运行实验。
