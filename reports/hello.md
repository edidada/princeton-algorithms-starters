# Hello 入门实验报告

来源：[Princeton 官方可选 Hello 作业](https://coursera.cs.princeton.edu/algs4/assignments/hello/specification.php)，当前规格标注 optional/under construction。实现 HelloWorld、HelloGoodbye、RandomWord，分支 edidada/hello，独立 hello.yml，JDK17 Maven 多模块与Gradle。

RandomWord 按要求使用 StdIn/StdOut/StdRandom.bernoulli，读第 i 个词时以1/i概率替换当前词。任一第 k 个位置最终保留概率为 (1/k)×∏(j=k+1..n)(1−1/j)=1/n，故按出现位置均匀抽样；重复词仍按出现次数计算，不缓存词数组或列表。扫描耗时线性于输入字符，额外空间只保存当前词、冠军词和计数，不含库自身输入缓冲。

Actions 实际CLI检查问候顺序/逆序、空输入、单词、重复词、20000词输入，以及120次随机抽样是否覆盖首/中/尾；频数仅作观察，不把频数测试当成均匀性证明。Maven/Gradle均须构建成功。最终结果见最终提交完成态CI，不声称Coursera私有评分；本地未编译或执行实验。
