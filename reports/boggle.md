# Boggle 实验报告

来源：[Princeton 官方 Boggle 规格](https://coursera.cs.princeton.edu/algs4/assignments/boggle/specification.php)。原始 BoggleBoard、BoggleGame、字典及棋盘保持不变；实现 BoggleSolver.java，分支 edidada/boggle，独立 `.github/workflows/boggle.yml`，JDK17 同时 Maven 多模块和 Gradle。

构造26路 Trie，终端保存字典字符串；搜索沿八邻接格深度优先遍历，当前路径用 used 标记禁止复用格子。每访问 Q 格连续推进 Q/U 两条 Trie 边，因此一个 Q 格同时贡献两个字符。前缀不存在时立即剪枝，直接复用终端字符串避免路径字符串拼接，HashSet 合并不同路径形成的相同词。各次查询的棋盘、邻接表、标记和结果均独立，修改返回结果不影响字典和后续查询。

scoreOf 只判断字典成员和字符数，不依赖任何棋盘：3/4字母1分、5字母2分、6字母3分、7字母5分、8字母以上11分。字典中带裸Q的词仍可有分，但不会错误地从Q/Qu棋盘格中拼出。

远程验收：5组 JUnit，100张2×3棋盘无前缀剪枝地独立枚举全部简单路径，再与字典求交集；Q/QU、斜向相邻、不可复用、去重、全部计分档位及查询隔离。原始 algs4 字典两张示例棋盘分数33/84，完整 YAWL 字典的0至4540分原始压力棋盘。CLI 核对官方分数。性能程序预生成2000张随机Hasbro棋盘、预热三轮、测五轮，报告查询吞吐量中位数，并要求至少2000棋盘/秒；该计时不包含棋盘生成和字典构造，不能外推为任意硬件成绩。

Maven/Gradle、CLI、性能检查均在 Actions 执行，最终结论以最终提交完成态 CI 为准；未运行 Coursera 私有评分或可选交互游戏，本地未编译或执行实验。
