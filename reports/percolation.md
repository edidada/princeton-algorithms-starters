# Princeton Algorithms I：Percolation

实现官方API的Percolation与PercolationStats。采用官方starter随附algs4.jar中的WeightedQuickUnionUF，分别维护渗流连通和充满状态两套并查集：前者有上下虚节点，后者仅有顶部虚节点，避免渗流后backwash。每次open仅处理四邻接，重复打开不重复计数；坐标1-based，非法n/坐标抛IllegalArgumentException。

Monte Carlo按规格独立随机打开位置，记录首次渗流时的开放比例，计算样本标准差与1.96倍标准误的95%区间；单次试验标准差NaN。构造O(n²)，站点操作为weighted union-find的O(log n)，isOpen/计数O(1)。

JDK17，Maven父工程+percolation模块，同时提供Gradle。保留官方默认包API和两个可视化客户端，使用原归档中的algs4.jar；其他作业未加入编译模块，不把教师提供客户端当作本人实现。独立percolation.yml依次mvn verify、gradle test、运行Monte Carlo入口；本地不编译。

4组JUnit：1x1/重复open/backwash，200个随机小网格逐步对照独立BFS（每步核对每个站点与计数/渗流），非法输入，统计及区间恒等式。公开包没有课程私有评分器，因此这是API与独立oracle验收，不声称Coursera正式成绩；GUI客户端仅编译，未做人工交互验收。
