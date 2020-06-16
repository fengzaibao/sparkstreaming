create database log_analysis;

--需求1: 筛选出所有的 error,转存数据库 
--   id       level       method      content
use log_analysis;

CREATE TABLE `important_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_level` varchar(255) NOT NULL,
  `method` varchar(255) NOT NULL,
  `content` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
