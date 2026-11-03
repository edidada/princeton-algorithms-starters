# Slider Puzzle 实验报告

来源：[Princeton 官方 8 Puzzle 规格](https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php)，原始发行包及输入数据保持不变。实现 Board.java 与 Solver.java；JDK17，Maven 多模块和 Gradle 共用源码，独立 `.github/workflows/8puzzle.yml`。

Board 防御复制二维输入，缓存 Hamming、Manhattan 和哈希；相等性逐格比较，邻居只交换空格，twin 交换两个非空块。所有方法复杂度 O(n²) 或更低。

Solver 使用 algs4 MinPQ，以 g+Manhattan 为优先级、Manhattan 作平局排序。初始棋盘与 twin 两路 A* 交替扩展，恰有一路可到目标；原棋盘先到目标时沿前驱回溯最短路径，twin 先到时返回不可解。跳过直接回退，缓存每个状态最优 g 并丢弃过期节点，避免重复搜索；路径只读。Manhattan 一致且可采纳，因此弹出目标时所得步数最优。

远程验收：全部24种2×2排列与独立字符串 BFS；100个3×3状态与深度16 BFS 对照；官方 puzzle3x3-00 至 -31 的全部32个最优步数样例和无解例；每条解逐步检查合法空格移动、起点终点及无环；4×4、5×5、10×10两步棋盘。运行原始 PuzzleChecker 和 Solver CLI，Maven/Gradle 均须通过。最终结论以实验分支最终提交的完成态 CI 为准；不声称通过 Coursera 私有评分或任意高难度大棋盘的时间界限。本地未编译或执行实验。
