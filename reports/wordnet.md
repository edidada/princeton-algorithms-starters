# WordNet 实验报告

来源：[Princeton 官方 WordNet 规格](https://coursera.cs.princeton.edu/algs4/assignments/wordnet/specification.php)，原始词典、图数据和 bundled algs4.jar 保留。实现 SAP.java、WordNet.java、Outcast.java；分支 edidada/wordnet，独立 `.github/workflows/wordnet.yml`，JDK17 同时 Maven 多模块和 Gradle。

SAP 防御复制输入 Digraph；两次多源 BFS 得到到每个顶点的最短距离，枚举共同可达顶点最小化距离之和。支持环、断开图、重复源、空源集合，检查 null 和越界顶点，输入 iterable 只遍历一次。每次查询 O(V+E)，额外空间 O(V)，图存储 O(V+E)；并列祖先返回最小编号之一。没有保存任意数量的查询缓存。

WordNet 使用 TreeMap 建立 noun→全部 synset id 索引，保留一词多义及同义词。读取 CSV 只切分前两个逗号，忽略含逗号 gloss；拓扑排序检查无环、统计唯一出度零根。有限 DAG 恰有一个出度零顶点时，每个顶点都能沿边到达该根。迭代实现可处理深链，避免递归栈溢出。构造复杂度 O(输入大小×log词数)，查询 noun O(log词数)，distance/sap O(V+E)；noun 枚举只读，图和索引不外泄。

Outcast 累加各 noun 到其他 noun 的距离，每个无序对只求一次，使用 long 累计防止总和溢出，总时间 O(k²(V+E))。同分返回输入中较早者。

远程验收：6组 JUnit，100张随机含环/断开有向图与独立 Floyd–Warshall 对照全部点对和多源集合；原始 digraph1 样例；防御复制、一次性 iterable、空集合和非法参数；人工多义词数据；四类不合法图、原始无效 DAG 数据及20000节点深链；完整官方词典的 table/bed/potato Outcast。CLI 实际读取标准输入及原始文件，Maven/Gradle 均须通过。最终状态以最终提交完成态 CI 为准，不声称 Coursera 私有评分通过。本地未编译或执行实验。
