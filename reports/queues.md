# Princeton Algorithms I：Deques and Randomized Queues

实现Deque双向链表（所有端点操作最坏O(1)）、RandomizedQueue动态数组（enqueue/dequeue摊销O(1)，sample最坏O(1)）及独立随机快照迭代器。删除尾引用避免对象滞留，迭代器耗尽抛NoSuchElementException，remove不支持，null入队抛IllegalArgumentException。Permutation用reservoir sampling，仅保留k项，随机输出不重复的输入位置；输入字符串相同不代表重复选择同一位置。

JDK17 Maven多模块和Gradle均在独立queues.yml运行4组JUnit：25000次双端操作对照JDK ArrayDeque，异常/空队转换，10轮1000项扩缩容与快照独立性，宽阈值随机采样/全部6种三元素迭代排列。实际Java CLI检查k=0、k=n、重复字符串、流式输入成员与数量、输入不足失败。公开ZIP未包含私有评分器，结果不称正式课程成绩；原教材/素材保留，本地未运行Java或构建。
