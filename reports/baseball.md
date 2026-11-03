# Baseball Elimination 实验报告

来源：[Princeton 官方规格](https://coursera.cs.princeton.edu/algs4/assignments/baseball/specification.php)，使用原始 teams 数据与 bundled algs4.jar。实现 BaseballElimination.java；分支 edidada/baseball，独立 `.github/workflows/baseball.yml`，JDK17 同时 Maven 多模块和 Gradle。

对目标球队 x，胜场上限 M=w[x]+r[x]，不是只累加赛区内比赛。若已有球队胜场超过 M，返回该单球队作为平凡淘汰证明。否则忽略与 x 有关的比赛（令 x 全胜是最有利情况），将其余比赛归约为最大流：源到比赛容量 g[i][j]，比赛到两球队容量无限，球队到汇容量 M-w[i]；使用官方 algs4 FlowNetwork、FlowEdge、FordFulkerson。只建立有剩余比赛的 game 节点。

所有比赛流量饱和时存在不超过 M 的胜场分配，因此 x 可并列第一；不饱和时，取最小割源侧球队集合 R。其已有胜场之和加 R 内必须产生的胜场大于 |R|M，故至少一队超过 x 上限。API 返回只读球队枚举和只读证明，对未知/null球队抛 IllegalArgumentException，未淘汰返回 null。按球队惰性缓存结果，重复读取不重建流图。胜场上限与胜场总和用 long，防止 int 加法溢出。

每队流图顶点/边最坏均为 Θ(n²)，按题目给定最大流上界 VE²，求解最坏 O(n⁶)；矩阵、单流图和缓存证明总空间 O(n²)。不宣称所有球队同时求解具有同样的时间界限。

远程验收：5组 JUnit，官方四/五球队例子，250个随机小联赛穷举剩余比赛所有胜负分配，与最大流判断独立对照；返回证明逐个用整数算术验证严格不等式。另测单球队、并列、赛区外比赛、多个同队比赛、超过 int 的胜场上限、非法球队与不可变返回值；原始最大60队联赛验证证明。实际 CLI 运行 teams4/5/12。Maven/Gradle 均须通过，最终结论以分支最终提交完成态 CI 为准；未运行 Coursera 私有评分，本地未编译或执行实验。
