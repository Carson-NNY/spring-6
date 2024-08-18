-- 这里只有当我们之前run 过sql创建的命令并且Mysql里面已经存在了这个表的时候, 我们需要这个file去更新表
alter table customer
    add email varchar(255);

