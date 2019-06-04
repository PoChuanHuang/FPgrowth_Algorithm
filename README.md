# Frequent Pattern growth Algorithm for Association Rule Mining
This is a project in my class named Algorithm.
You can choose different database in this code.
I provide three database to choose.
Pleace Download the three txt file in your computer,and write correct file path in gFilePath.
1. BMS1_itemset_mining.txt
2. mushrooms.txt
3. T10I4D100K.txt
```gherkin=
public static String gFilePath = "";
```
You can set the threshold value by youself,the value is percent,you can get the different result.(ex:80 is 80%)
```gherkin=
public static float threshold=80;
```

In console,you can see...
1. The File Name
2. The threshold
3. The FP_Tree
ccc
4. Product Combination
Sepically,You can see each product combination that have a tree for it.
5. TotalCount
6. Using Time
