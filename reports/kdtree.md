# Kd-Trees 实验报告

来源：[Princeton 官方 Kd-Trees 规格](https://coursera.cs.princeton.edu/algs4/assignments/kdtree/specification.php)。原始输入、可视化客户端及 bundled algs4.jar 保留。实现 PointSET.java 和 KdTree.java，分支 edidada/kdtree，独立 `.github/workflows/kdtree.yml`，JDK17 同时采用 Maven 多模块和 Gradle。

PointSET 按要求使用 java.util.TreeSet 红黑树，插入/存在查询 O(log n)，最近邻和范围查询 O(n)。KdTree 根按 x、下层交替按 y/x 划分；相同分割坐标走右侧，完全重复点不新增。每个节点保存覆盖子树的闭矩形，范围不相交时剪枝；最近邻先访问查询点所在侧，只有矩形距离可能改善当前解时才扩展。等距时任意最近点均符合规格。插入/contains 的代价 O(h)，范围/最近邻最坏 O(n)，不声称未平衡树具有最坏对数时间。

遍历用显式栈避免退化树调用栈溢出，范围结果为独立快照。draw 先绘制红色竖分割和蓝色横分割，再统一绘制黑色点，防止后续分割线覆盖已有点。

验收在远程 Actions 执行：5组 JUnit 覆盖空树/重复/全部 null 参数、11×11边界网格和零面积矩形、12轮800点各150次独立暴力几何对照、8000点有序退化树、官方 input100K 十万点和32次查询。比较最近邻距离以允许等距合法答案，范围检查完整集合且不得重复。虚拟显示器运行 draw，保存 kdtree.png/pointset.png 并检查红蓝黑像素存在。Maven/Gradle 均须通过，最终结论以分支最终提交的完成态 CI 为准；未运行 Coursera 私有评分，也未完成可选百万点性能基准。本地不编译或执行实验。
