# Effisto
An efficient framework for generic NoSQL data stores:

The goal of this work is to define a general approach for enabling efficient queries and storage of semi-structured, hierarchical data, with reference to several NoSQL systems. Data is stored and accessed by using a common, XPath-like query language, which gets seamlessly translated into sys- tems native operations. Likewise, our platform maps objects data into systems native structures by exploiting high-level, declarative translations.
In detail, we want to pursue the following objectives:
1. Store and access data by using a uniform interface, hiding the specific details of the underlying systems.
2. Deploy data on the specific systems by using declarative, reusable translations between our model and the specific ones.
3. Define translations that store data in an effective way according to the systems native structures, operations, and best practices.
4. Enable efficient accesses by supporting the creation of indexes and secondary structures via our common in- terface.
5. Define smart strategies for using those supporting structures according to query requirements.
