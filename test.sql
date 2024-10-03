-- MySQL dump 10.13  Distrib 8.3.0, for macos14.2 (arm64)
--
-- Host: localhost    Database: test
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) DEFAULT NULL,
  `city` int DEFAULT NULL,
  `state` int DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `altitude` float DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (33,'7406754a-ff71-4a3a-855b-b403e8eeb544',1,0,NULL,NULL,1726506666268,1,1726506666268,1),(34,'6ccdf3ce-7d8f-4f6f-abb4-69076527532a',1,1,NULL,NULL,1727507769879,1,1727839551477,0),(35,'90945ce0-dd4a-4d25-b6bb-247fd7436ec4',1,1,NULL,NULL,1727577468377,1,1727577468377,1),(43,'33fed797-64fc-4957-baf8-1bab49ef6e7a',1,1,NULL,NULL,1727840298841,0,1727840298841,0),(44,'f76e4bfd-0ee1-491a-a43b-f456195c823e',1,1,NULL,NULL,1727840481276,0,1727840481276,0),(45,'68c034c5-3b39-4dcf-b138-fd7cdcfe2c4a',1,1,NULL,NULL,1727840857337,0,1727840857337,0),(46,'73d2f490-759c-42fa-b0eb-b1aa33b4f2d8',1,1,NULL,NULL,1727843172861,0,1727843172861,0),(47,'ccb8164f-3337-4b7e-9f1d-9c7e9be06a4a',1,1,NULL,NULL,1727848104102,0,1727848104102,0),(48,'70d9aa4c-c6a7-4ce9-b057-fb536e62e837',1,1,NULL,NULL,1727848161830,0,1727848161830,0),(49,'9b998d10-cbb6-4a59-b5ed-c8950672f23b',1,1,NULL,NULL,1727848197889,0,1727848197889,0),(50,'3a7f06d4-f1fc-4a79-93cb-4ace56e69659',1,1,NULL,NULL,1727853641793,0,1727853641793,0);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city_name` varchar(50) DEFAULT NULL,
  `state_id` int DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'jaipur',1,'A');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_permissions`
--

DROP TABLE IF EXISTS `group_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` int DEFAULT NULL,
  `permission_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_permissions`
--

LOCK TABLES `group_permissions` WRITE;
/*!40000 ALTER TABLE `group_permissions` DISABLE KEYS */;
INSERT INTO `group_permissions` VALUES (779,2,13),(780,2,14),(781,2,15),(782,2,16),(783,2,17),(784,2,18),(785,2,19),(786,2,20),(787,2,21),(788,2,22),(789,2,23),(790,2,24),(791,2,25),(792,2,2),(793,2,29),(794,2,30),(795,2,26),(796,2,7),(797,2,8),(798,2,9),(799,2,10),(800,2,11),(801,2,12),(802,2,32),(803,2,34),(804,2,27),(805,2,33),(806,2,1),(807,2,31),(808,2,4),(809,2,3),(810,2,6),(811,2,5),(812,9,13),(813,9,14),(814,9,15),(815,9,16),(816,9,17),(817,9,18),(818,9,19),(819,9,20),(820,9,21),(821,9,22),(822,9,23),(823,9,24),(824,9,25),(825,9,1),(826,9,2),(827,9,3),(828,9,4),(829,9,5),(830,9,6),(831,9,31),(832,9,34),(833,9,29),(834,9,30),(835,9,26),(836,9,7),(837,9,8),(838,9,9),(839,9,10),(840,9,11),(841,9,12),(842,9,32),(843,9,27),(844,9,33),(845,0,13),(846,0,14),(847,0,15),(848,0,16),(849,0,17),(850,0,18),(851,0,19),(852,0,20),(853,0,21),(854,0,22),(855,0,23),(856,0,24),(857,0,25),(858,0,1),(859,0,2),(860,0,3),(861,0,4),(862,0,5),(863,0,6),(864,0,31),(865,0,34),(866,0,29),(867,0,30),(868,0,26),(869,0,7),(870,0,8),(871,0,9),(872,0,10),(873,0,11),(874,0,12),(875,0,32),(876,0,27),(877,0,33),(881,0,13),(882,0,14),(883,0,15),(884,0,NULL),(885,0,17),(886,0,18),(887,0,19),(888,0,20),(889,0,21),(890,0,22),(891,0,NULL),(892,0,24),(893,0,25),(894,0,1),(895,0,2),(896,0,3),(897,0,NULL),(898,0,5),(899,0,6),(900,0,31),(901,0,34),(902,0,29),(903,0,30),(904,0,26),(905,0,7),(906,0,8),(907,0,9),(908,0,NULL),(909,0,11),(910,0,12),(911,0,32),(912,0,27),(913,0,33),(914,0,35),(915,0,36),(916,0,13),(917,0,14),(918,0,15),(919,0,17),(920,0,18),(921,0,19),(922,0,20),(923,0,21),(924,0,22),(925,0,24),(926,0,25),(927,0,1),(928,0,2),(929,0,3),(930,0,5),(931,0,6),(932,0,7),(933,0,31),(934,0,34),(935,0,35),(936,0,36),(937,0,29),(938,0,30),(939,0,26),(940,0,8),(941,0,9),(942,0,11),(943,0,12),(944,0,32),(945,0,27),(946,0,33),(1337,0,13),(1338,0,14),(1339,0,15),(1340,0,NULL),(1341,0,17),(1342,0,18),(1343,0,19),(1344,0,20),(1345,0,21),(1346,0,22),(1347,0,NULL),(1348,0,24),(1349,0,25),(1350,0,1),(1351,0,2),(1352,0,3),(1353,0,NULL),(1354,0,5),(1355,0,6),(1356,0,31),(1357,0,34),(1358,0,29),(1359,0,30),(1360,0,26),(1361,0,7),(1362,0,8),(1363,0,9),(1364,0,NULL),(1365,0,11),(1366,0,12),(1367,0,32),(1368,0,27),(1369,0,33),(1370,0,13),(1371,0,14),(1372,0,15),(1373,0,NULL),(1374,0,17),(1375,0,18),(1376,0,19),(1377,0,20),(1378,0,21),(1379,0,22),(1380,0,NULL),(1381,0,24),(1382,0,25),(1383,0,1),(1384,0,2),(1385,0,3),(1386,0,NULL),(1387,0,5),(1388,0,6),(1389,0,31),(1390,0,34),(1391,0,29),(1392,0,30),(1393,0,26),(1394,0,7),(1395,0,8),(1396,0,9),(1397,0,NULL),(1398,0,11),(1399,0,12),(1400,0,32),(1401,0,27),(1402,0,33),(1403,0,35),(1404,0,36),(1405,0,13),(1406,0,14),(1407,0,15),(1408,0,17),(1409,0,18),(1410,0,19),(1411,0,20),(1412,0,21),(1413,0,22),(1414,0,24),(1415,0,25),(1416,0,1),(1417,0,2),(1418,0,3),(1419,0,5),(1420,0,6),(1421,0,7),(1422,0,31),(1423,0,34),(1424,0,35),(1425,0,36),(1426,0,29),(1427,0,30),(1428,0,26),(1429,0,8),(1430,0,9),(1431,0,11),(1432,0,12),(1433,0,32),(1434,0,27),(1435,0,33),(1467,11,13),(1468,11,14),(1469,11,15),(1470,11,17),(1471,11,18),(1472,11,19),(1473,11,20),(1474,11,21),(1475,11,22),(1476,11,24),(1477,11,25),(1478,11,1),(1479,11,2),(1480,11,3),(1481,11,5),(1482,11,6),(1483,11,7),(1484,11,31),(1485,11,34),(1486,11,35),(1487,11,36),(1488,11,29),(1489,11,30),(1490,11,26),(1491,11,8),(1492,11,9),(1493,11,11),(1494,11,12),(1495,11,32),(1496,11,27),(1497,11,33),(1498,11,37),(1499,0,13),(1500,0,14),(1501,0,15),(1502,0,NULL),(1503,0,17),(1504,0,18),(1505,0,19),(1506,0,20),(1507,0,21),(1508,0,22),(1509,0,NULL),(1510,0,24),(1511,0,25),(1512,0,1),(1513,0,2),(1514,0,3),(1515,0,NULL),(1516,0,5),(1517,0,6),(1518,0,31),(1519,0,34),(1520,0,29),(1521,0,30),(1522,0,26),(1523,0,7),(1524,0,8),(1525,0,9),(1526,0,NULL),(1527,0,11),(1528,0,12),(1529,0,32),(1530,0,27),(1531,0,33),(1532,0,13),(1533,0,14),(1534,0,15),(1535,0,NULL),(1536,0,17),(1537,0,18),(1538,0,19),(1539,0,20),(1540,0,21),(1541,0,22),(1542,0,NULL),(1543,0,24),(1544,0,25),(1545,0,1),(1546,0,2),(1547,0,3),(1548,0,NULL),(1549,0,5),(1550,0,6),(1551,0,31),(1552,0,34),(1553,0,29),(1554,0,30),(1555,0,26),(1556,0,7),(1557,0,8),(1558,0,9),(1559,0,NULL),(1560,0,11),(1561,0,12),(1562,0,32),(1563,0,27),(1564,0,33),(1565,0,35),(1566,0,36),(1567,0,13),(1568,0,14),(1569,0,15),(1570,0,17),(1571,0,18),(1572,0,19),(1573,0,20),(1574,0,21),(1575,0,22),(1576,0,24),(1577,0,25),(1578,0,1),(1579,0,2),(1580,0,3),(1581,0,5),(1582,0,6),(1583,0,7),(1584,0,31),(1585,0,34),(1586,0,35),(1587,0,36),(1588,0,29),(1589,0,30),(1590,0,26),(1591,0,8),(1592,0,9),(1593,0,11),(1594,0,12),(1595,0,32),(1596,0,27),(1597,0,33),(1598,0,13),(1599,0,14),(1600,0,15),(1601,0,NULL),(1602,0,17),(1603,0,18),(1604,0,19),(1605,0,20),(1606,0,21),(1607,0,22),(1608,0,NULL),(1609,0,24),(1610,0,25),(1611,0,1),(1612,0,2),(1613,0,3),(1614,0,NULL),(1615,0,5),(1616,0,6),(1617,0,31),(1618,0,34),(1619,0,29),(1620,0,30),(1621,0,26),(1622,0,7),(1623,0,8),(1624,0,9),(1625,0,NULL),(1626,0,11),(1627,0,12),(1628,0,32),(1629,0,27),(1630,0,33),(1631,0,13),(1632,0,14),(1633,0,15),(1634,0,NULL),(1635,0,17),(1636,0,18),(1637,0,19),(1638,0,20),(1639,0,21),(1640,0,22),(1641,0,NULL),(1642,0,24),(1643,0,25),(1644,0,1),(1645,0,2),(1646,0,3),(1647,0,NULL),(1648,0,5),(1649,0,6),(1650,0,31),(1651,0,34),(1652,0,29),(1653,0,30),(1654,0,26),(1655,0,7),(1656,0,8),(1657,0,9),(1658,0,NULL),(1659,0,11),(1660,0,12),(1661,0,32),(1662,0,27),(1663,0,33),(1664,0,35),(1665,0,36),(1666,0,13),(1667,0,14),(1668,0,15),(1669,0,17),(1670,0,18),(1671,0,19),(1672,0,20),(1673,0,21),(1674,0,22),(1675,0,24),(1676,0,25),(1677,0,1),(1678,0,2),(1679,0,3),(1680,0,5),(1681,0,6),(1682,0,7),(1683,0,31),(1684,0,34),(1685,0,35),(1686,0,36),(1687,0,29),(1688,0,30),(1689,0,26),(1690,0,8),(1691,0,9),(1692,0,11),(1693,0,12),(1694,0,32),(1695,0,27),(1696,0,33),(1697,0,37),(1698,12,13),(1699,12,14),(1700,12,15),(1701,12,17),(1702,12,18),(1703,12,19),(1704,12,20),(1705,12,21),(1706,12,22),(1707,12,24),(1708,12,25),(1709,12,1),(1710,12,2),(1711,12,3),(1712,12,5),(1713,12,6),(1714,12,7),(1715,12,31),(1716,12,34),(1717,12,35),(1718,12,36),(1719,12,29),(1720,12,30),(1721,12,26),(1722,12,8),(1723,12,9),(1724,12,11),(1725,12,12),(1726,12,32),(1727,12,37),(1728,12,27),(1729,12,33),(1730,0,13),(1731,0,14),(1732,0,15),(1733,0,NULL),(1734,0,17),(1735,0,18),(1736,0,19),(1737,0,20),(1738,0,21),(1739,0,22),(1740,0,NULL),(1741,0,24),(1742,0,25),(1743,0,1),(1744,0,2),(1745,0,3),(1746,0,NULL),(1747,0,5),(1748,0,6),(1749,0,31),(1750,0,34),(1751,0,29),(1752,0,30),(1753,0,26),(1754,0,7),(1755,0,8),(1756,0,9),(1757,0,NULL),(1758,0,11),(1759,0,12),(1760,0,32),(1761,0,27),(1762,0,33),(1763,0,13),(1764,0,14),(1765,0,15),(1766,0,NULL),(1767,0,17),(1768,0,18),(1769,0,19),(1770,0,20),(1771,0,21),(1772,0,22),(1773,0,NULL),(1774,0,24),(1775,0,25),(1776,0,1),(1777,0,2),(1778,0,3),(1779,0,NULL),(1780,0,5),(1781,0,6),(1782,0,31),(1783,0,34),(1784,0,29),(1785,0,30),(1786,0,26),(1787,0,7),(1788,0,8),(1789,0,9),(1790,0,NULL),(1791,0,11),(1792,0,12),(1793,0,32),(1794,0,27),(1795,0,33),(1796,0,35),(1797,0,36),(1798,0,13),(1799,0,14),(1800,0,15),(1801,0,17),(1802,0,18),(1803,0,19),(1804,0,20),(1805,0,21),(1806,0,22),(1807,0,24),(1808,0,25),(1809,0,1),(1810,0,2),(1811,0,3),(1812,0,5),(1813,0,6),(1814,0,7),(1815,0,31),(1816,0,34),(1817,0,35),(1818,0,36),(1819,0,29),(1820,0,30),(1821,0,26),(1822,0,8),(1823,0,9),(1824,0,11),(1825,0,12),(1826,0,32),(1827,0,27),(1828,0,33),(1829,0,13),(1830,0,14),(1831,0,15),(1832,0,NULL),(1833,0,17),(1834,0,18),(1835,0,19),(1836,0,20),(1837,0,21),(1838,0,22),(1839,0,NULL),(1840,0,24),(1841,0,25),(1842,0,1),(1843,0,2),(1844,0,3),(1845,0,NULL),(1846,0,5),(1847,0,6),(1848,0,31),(1849,0,34),(1850,0,29),(1851,0,30),(1852,0,26),(1853,0,7),(1854,0,8),(1855,0,9),(1856,0,NULL),(1857,0,11),(1858,0,12),(1859,0,32),(1860,0,27),(1861,0,33),(1862,0,13),(1863,0,14),(1864,0,15),(1865,0,NULL),(1866,0,17),(1867,0,18),(1868,0,19),(1869,0,20),(1870,0,21),(1871,0,22),(1872,0,NULL),(1873,0,24),(1874,0,25),(1875,0,1),(1876,0,2),(1877,0,3),(1878,0,NULL),(1879,0,5),(1880,0,6),(1881,0,31),(1882,0,34),(1883,0,29),(1884,0,30),(1885,0,26),(1886,0,7),(1887,0,8),(1888,0,9),(1889,0,NULL),(1890,0,11),(1891,0,12),(1892,0,32),(1893,0,27),(1894,0,33),(1895,0,35),(1896,0,36),(1897,0,13),(1898,0,14),(1899,0,15),(1900,0,17),(1901,0,18),(1902,0,19),(1903,0,20),(1904,0,21),(1905,0,22),(1906,0,24),(1907,0,25),(1908,0,1),(1909,0,2),(1910,0,3),(1911,0,5),(1912,0,6),(1913,0,7),(1914,0,31),(1915,0,34),(1916,0,35),(1917,0,36),(1918,0,29),(1919,0,30),(1920,0,26),(1921,0,8),(1922,0,9),(1923,0,11),(1924,0,12),(1925,0,32),(1926,0,27),(1927,0,33),(1928,0,13),(1929,0,14),(1930,0,15),(1931,0,NULL),(1932,0,17),(1933,0,18),(1934,0,19),(1935,0,20),(1936,0,21),(1937,0,22),(1938,0,NULL),(1939,0,24),(1940,0,25),(1941,0,1),(1942,0,2),(1943,0,3),(1944,0,NULL),(1945,0,5),(1946,0,6),(1947,0,31),(1948,0,34),(1949,0,29),(1950,0,30),(1951,0,26),(1952,0,7),(1953,0,8),(1954,0,9),(1955,0,NULL),(1956,0,11),(1957,0,12),(1958,0,32),(1959,0,27),(1960,0,33),(1961,0,13),(1962,0,14),(1963,0,15),(1964,0,NULL),(1965,0,17),(1966,0,18),(1967,0,19),(1968,0,20),(1969,0,21),(1970,0,22),(1971,0,NULL),(1972,0,24),(1973,0,25),(1974,0,1),(1975,0,2),(1976,0,3),(1977,0,NULL),(1978,0,5),(1979,0,6),(1980,0,31),(1981,0,34),(1982,0,29),(1983,0,30),(1984,0,26),(1985,0,7),(1986,0,8),(1987,0,9),(1988,0,NULL),(1989,0,11),(1990,0,12),(1991,0,32),(1992,0,27),(1993,0,33),(1994,0,35),(1995,0,36),(1996,0,13),(1997,0,14),(1998,0,15),(1999,0,17),(2000,0,18),(2001,0,19),(2002,0,20),(2003,0,21),(2004,0,22),(2005,0,24),(2006,0,25),(2007,0,1),(2008,0,2),(2009,0,3),(2010,0,5),(2011,0,6),(2012,0,7),(2013,0,31),(2014,0,34),(2015,0,35),(2016,0,36),(2017,0,29),(2018,0,30),(2019,0,26),(2020,0,8),(2021,0,9),(2022,0,11),(2023,0,12),(2024,0,32),(2025,0,27),(2026,0,33),(2027,0,13),(2028,0,14),(2029,0,15),(2030,0,NULL),(2031,0,17),(2032,0,18),(2033,0,19),(2034,0,20),(2035,0,21),(2036,0,22),(2037,0,NULL),(2038,0,24),(2039,0,25),(2040,0,1),(2041,0,2),(2042,0,3),(2043,0,NULL),(2044,0,5),(2045,0,6),(2046,0,31),(2047,0,34),(2048,0,29),(2049,0,30),(2050,0,26),(2051,0,7),(2052,0,8),(2053,0,9),(2054,0,NULL),(2055,0,11),(2056,0,12),(2057,0,32),(2058,0,27),(2059,0,33),(2060,0,13),(2061,0,14),(2062,0,15),(2063,0,NULL),(2064,0,17),(2065,0,18),(2066,0,19),(2067,0,20),(2068,0,21),(2069,0,22),(2070,0,NULL),(2071,0,24),(2072,0,25),(2073,0,1),(2074,0,2),(2075,0,3),(2076,0,NULL),(2077,0,5),(2078,0,6),(2079,0,31),(2080,0,34),(2081,0,29),(2082,0,30),(2083,0,26),(2084,0,7),(2085,0,8),(2086,0,9),(2087,0,NULL),(2088,0,11),(2089,0,12),(2090,0,32),(2091,0,27),(2092,0,33),(2093,0,35),(2094,0,36),(2095,0,13),(2096,0,14),(2097,0,15),(2098,0,17),(2099,0,18),(2100,0,19),(2101,0,20),(2102,0,21),(2103,0,22),(2104,0,24),(2105,0,25),(2106,0,1),(2107,0,2),(2108,0,3),(2109,0,5),(2110,0,6),(2111,0,7),(2112,0,31),(2113,0,34),(2114,0,35),(2115,0,36),(2116,0,29),(2117,0,30),(2118,0,26),(2119,0,8),(2120,0,9),(2121,0,11),(2122,0,12),(2123,0,32),(2124,0,27),(2125,0,33),(2126,0,37),(2127,0,38);
/*!40000 ALTER TABLE `group_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `groups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `name_2` (`name`),
  UNIQUE KEY `name_3` (`name`),
  UNIQUE KEY `name_4` (`name`),
  UNIQUE KEY `name_5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (0,'0967d2c4-6606-4363-a06a-16edf5676ef4','Super Admin',NULL,1727365382117,1,1727365382117,1),(11,'55e5b889-d032-46dd-9ed7-ad73a44efb9f','Admin',NULL,1727794343550,0,1727794343550,0),(12,'b58c6a1a-7de6-493c-9b6e-ccf9b6945401','Test',NULL,1727796261686,57,1727796261686,57);
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `wholesale_id` int DEFAULT NULL,
  `label` enum('O','N') DEFAULT NULL,
  `price` float NOT NULL,
  `discount` float DEFAULT NULL,
  `description` text NOT NULL,
  `avatar` text,
  `rating` float DEFAULT NULL,
  `status` enum('A','D') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint NOT NULL,
  `created_by` int NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `slug` varchar(50) DEFAULT NULL,
  `in_stock` enum('Y','N') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `wholesale_id` (`wholesale_id`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`wholesale_id`) REFERENCES `store` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'Test2',1,'O',200,0,'df',NULL,0,'A','N',0,1,1727592635857,1,'sfdssdfsdf','Y'),(52,'Testy',17,'N',80,12,'bikaji orignal ',NULL,0,'A','N',1727453460457,1,1727453460457,1,'0cbf4162-a5df-409b-b90c-93873cfad5f2','Y'),(53,'Test',18,'O',1,1,'test','30130cbd-5ce9-42d2-bcc0-79b85fcb72c7nareshyoutubelogo.png',0,'A','N',1727710932262,1,1727711879639,1,'30130cbd-5ce9-42d2-bcc0-79b85fcb72c7','N'),(54,'test',18,'N',1234,1234,'test','5d7ef70e-207c-4a99-8505-18e505750710Frame_31_(9).png',0,'A','N',1727711379799,1,1727856782393,0,'5d7ef70e-207c-4a99-8505-18e505750710','Y'),(55,'Hello',18,'O',3544,545,'dfsdfs','37f1bf23-cfdb-4770-a28c-2a5dcf3f10cdFrame_31_(9).png',0,'A','Y',1727711402355,1,1727711438966,1,'37f1bf23-cfdb-4770-a28c-2a5dcf3f10cd','N');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_comments`
--

DROP TABLE IF EXISTS `item_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) NOT NULL,
  `item_id` int DEFAULT NULL,
  `store_id` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `message` text,
  `created_at` mediumtext,
  `updated_at` mediumtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_comments`
--

LOCK TABLES `item_comments` WRITE;
/*!40000 ALTER TABLE `item_comments` DISABLE KEYS */;
INSERT INTO `item_comments` VALUES (1,'sdfsdsdfsd',1,1,0,0,'N','Test 124','1727889480050','1727889480050'),(2,'sdfdsfs',1,1,1,0,'N','dfgfd','1727889480050','1727889480050'),(3,'sdfsdfsd',1,1,1,0,'N','reply','1727889480050','1727889480050');
/*!40000 ALTER TABLE `item_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_report`
--

DROP TABLE IF EXISTS `item_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_id` int DEFAULT NULL,
  `store_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `message` text,
  `created_at` mediumtext,
  `updated_at` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_report`
--

LOCK TABLES `item_report` WRITE;
/*!40000 ALTER TABLE `item_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `item_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `permission` varchar(50) DEFAULT NULL,
  `access_url` text,
  `permission_for` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'List','/admin/auth/all','User'),(2,'Create','/admin/auth/add','User'),(3,'Edit','/admin/auth/update','User'),(5,'Status','/admin/auth/status','User'),(6,'Delete','/admin/auth/delete','User'),(7,'Retailers List','/admin/auth/R/all','User'),(8,'Create','/admin/store/create','Store'),(9,'Edit','/admin/store/update','Store'),(11,'Status','/admin/store/status','Store'),(12,'Delete','/admin/store/delete','Store'),(13,'List','/admin/item/all','Item'),(14,'Create','/admin/item/add','Item'),(15,'Edit','/admin/item/update','Item'),(17,'Status','/admin/item/status','Item'),(18,'Delete','/admin/item/delete','Item'),(19,'Stock','/admin/item/stock','Item'),(20,'List','/group/all','Group'),(21,'Create','/group/create','Group'),(22,'Edit','/group/update','Group'),(24,'Status','/group/status','Group'),(25,'Delete','/group/delete','Group'),(26,'List','/group/permissions/all','Permissions'),(27,'List','/admin/dashboard/counts','Dashboard'),(29,'City List','/admin/address/city','Address'),(30,'State List','/admin/address/state','Address'),(31,'Groups','/admin/auth/groups','User'),(32,'Get Store By User','/admin/store/detailbyuser','Store'),(33,'Stores','/admin/dashboard/graph/months','Dashboard'),(34,'Password Reset','/admin/auth/password','User'),(35,'Staffs List','/admin/auth/S/all','User'),(36,'Wholesalers List','/admin/auth/W/all','User'),(37,'List','/admin/store/all','Store'),(38,'All Users List','/admin/auth/A/all','User');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slips`
--

DROP TABLE IF EXISTS `slips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slips` (
  `id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) NOT NULL,
  `item_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `wholesale_id` int DEFAULT NULL,
  `status` enum('S','P') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slips`
--

LOCK TABLES `slips` WRITE;
/*!40000 ALTER TABLE `slips` DISABLE KEYS */;
/*!40000 ALTER TABLE `slips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `state`
--

DROP TABLE IF EXISTS `state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `state` (
  `id` int NOT NULL AUTO_INCREMENT,
  `state_name` varchar(50) DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `state`
--

LOCK TABLES `state` WRITE;
/*!40000 ALTER TABLE `state` DISABLE KEYS */;
INSERT INTO `state` VALUES (1,'Rajasthan','A');
/*!40000 ALTER TABLE `state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `slug` varchar(50) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `avtar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `address` int DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(12) DEFAULT NULL,
  `discription` text,
  `rating` float DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `store_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,42,'test','Swami Kirana Store',NULL,33,'naresh@gmail.com','9145808226','dummy data',4,'A','N',1723998053886,NULL,1723998053886,NULL),(17,47,'92d8d129-f9b8-4ca1-b0ce-31554ddb1ef0','test kirana store',NULL,33,'testkirana@gmail.com','9876543219','test shop created',0,'D','Y',1726506666296,1,1726506666296,1),(18,51,'f66e37bd-f038-4e93-b8d4-29b7da8b495b','Suresh Kirana Store','f66e37bd-f038-4e93-b8d4-29b7da8b495bWhatsApp_Image_2024-04-07_at_18.24.07-removebg-preview.png',34,'swaminaresh993@gmail.com','9145808226','test',0,'A','N',1727507769906,1,1727839551479,0),(19,52,'983cf747-1b25-4b69-8813-7837638504d0','Sb parcel store',NULL,35,'sb993@gmail.com','09145808226','testing',0,'A','Y',1727577468411,1,1727577468411,1),(20,60,'54550dbf-d969-4941-9b53-4c231a6a70b5','Krihna',NULL,43,'swaminaresh993@gmail.com','9145808226','tesst',0,'A','Y',1727840298847,0,1727840298847,0),(21,65,'5bb0a925-3d52-4312-bc2d-edc4b1ee75fa','sfsdfsd',NULL,44,'sfsdf@gmail.com','9145808226','sdfsdfs',0,'A','Y',1727840481281,0,1727840481281,0),(22,76,'057162e6-fcea-4716-8906-2ba09d81bfba','sdfsdsdfsd',NULL,45,'sdfsdf@gmail.com','9145808227','sdfsd',0,'A','Y',1727840857351,0,1727840857351,0),(23,99,'3826b22e-2301-4997-845c-6aef29d364a2','vxdv',NULL,46,'dfsdfresh993@gmail.com','9145808226','sfsd',0,'A','N',1727843172870,0,1727843172870,0),(24,118,'5a1316d5-c5c8-4a6f-89e0-c9d0dedade83','sdfsd',NULL,47,'swaminaresh993@gmail.com','9145808226','fdsfsd',0,'A','Y',1727848104112,0,1727848104112,0),(25,119,'f1024d8d-3444-457f-8297-8b0758007bbd','sffsdd',NULL,48,'swaminaresh993@gmail.com','9145808226','sdfsdfsdfsd',0,'A','Y',1727848161835,0,1727848161835,0),(26,122,'d0091701-0d8d-40e9-9a6d-9370bf64bd23','sffsdd',NULL,49,'swaminaresh993@gmail.com','9145808226','sdfsdfsdfsd',0,'A','Y',1727848197893,0,1727848197893,0),(27,137,'842dab10-ee29-45b6-9240-ad860c17548b','Duplicate',NULL,50,'swaminaresh993@gmail.com','9146808226','test',0,'A','N',1727853641804,0,1727853641804,0);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_report`
--

DROP TABLE IF EXISTS `store_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `store_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `message` text,
  `created_at` mediumtext,
  `updated_at` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_report`
--

LOCK TABLES `store_report` WRITE;
/*!40000 ALTER TABLE `store_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `slug` varchar(50) DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(50) NOT NULL,
  `contact` varchar(12) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `avtar` text,
  `user_type` enum('R','S','W','A','SA') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `status` enum('A','D') DEFAULT NULL,
  `is_deleted` enum('Y','N') DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `contact_2` (`contact`),
  UNIQUE KEY `contact` (`contact`)
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (0,'dfgdsvdfge','naresh','naresh@gmail.com','9876543211','123456','dfgdsvdfgewrapperclasses.png','SA','A','N',1723998053886,1727790425013,NULL,0),(42,'327517f6-465d-4da2-8105-5dd9c62d678c','test','test@gmail.com','9876543214','test123',NULL,'W','A','N',1726505439062,1726505527769,1,1),(44,'ad708d9c-0251-4283-902f-20d9e043ae9b','test1','test1@gmail.com','9876543212','test123',NULL,'R','A','Y',1726505677696,1727462144600,1,1),(47,'efe075ef-9f87-494b-9030-2f598e05b496','test','testkirana@gmail.com','9876543210','test123','efe075ef-9f87-494b-9030-2f598e05b496pc3.png','R','D','Y',1726506666214,1727833829854,1,0),(51,'3634c3b1-2bd7-4c40-9b88-fb52f84b04b1','65664545','test@123gmail.com','123456789',NULL,NULL,'W','A','N',1727507769810,1727770970717,1,1),(52,'bad58cac-61f9-4b97-805a-fbfad48ebf5f','Sb parkash','sb993@gmail.com','09145808226',NULL,NULL,'W','A','Y',1727577468093,1727577468093,1,1),(53,'e53554b3-8174-438b-929c-d91c1875a0d3','naresh','swaminaresh993@gmail.com','987654323123','saleispower','e53554b3-8174-438b-929c-d91c1875a0d3var.png','R','A','N',1727760100676,1727799623723,1,57),(57,'a07ac7eb-183e-4cda-84f5-573af682bd20','Manisha Sharma','manisha@gmail.com','9876543215','saleispower',NULL,'A','A','N',1727789442142,1727802220086,0,57),(59,'7ef4b83b-6305-4da7-b428-a0c41bd3f174','Test User','test993@gmail.com','9876543219','saleispower',NULL,'R','A','N',1727834084274,1727856713664,0,0),(60,'bfddf7b4-017b-461c-ae16-4f958d81b096','Krishan Kumawat','krishna@gmail.com','9145808226','saleispower',NULL,'W','A','Y',1727840298795,1727840298795,0,0),(65,'11486586-5435-4616-a47d-a5ada22cd30a','sdfsd','sdfsdfsd@gmail.com','9145808227','saleispower',NULL,'W','A','Y',1727840481272,1727840481272,0,0),(66,'97c964ea-3f3f-4fc1-97f8-32e43e36408b','sdfsd','sdfsdf@gmail.com','9145808228','saleispower',NULL,'W','A','Y',1727840760214,1727840760214,0,0),(76,'ff7be47a-6f64-40bd-97dc-504302481979','sdfsdasda','sdfsdfsdfds@gmail.com','91458084545','saleispower',NULL,'W','A','Y',1727840857333,1727840857333,0,0),(77,'eeba2ef4-0b54-494e-8228-8c30cba00272','dfsdsfsd','sfsdf@gmail.com','91458082261','saleispower',NULL,'W','A','Y',1727841611373,1727841611373,0,0),(99,'c30a73ac-99d8-4fec-b775-d08fd0c73a88','vbcvbv','sdfsdh993@gmail.com','9145808229','saleispower',NULL,'W','A','N',1727843172843,1727843172843,0,0),(105,'fdd22546-47d7-4abd-97e6-691366228944','sdfsdfsd','sdfstest993@gmail.com','9145808245','saleispower',NULL,'W','A','Y',1727843919544,1727843919544,0,0),(118,'3991799b-1264-4eae-9c3e-f7c254d34b02','fdsdfd','gugug993@gmail.com','8145808226','saleispower',NULL,'W','A','Y',1727848104084,1727848104084,0,0),(119,'5e7f3058-f20b-495a-9bca-fa0d1d5f29c7','fdgfdgfd','sdfsdf993@gmail.com','945808226','saleispower',NULL,'W','A','Y',1727848161826,1727848161826,0,0),(122,'bcb77726-3662-4b4a-941e-bcc58d92ca44','fdgfdgfd','naresh993@gmail.com','995808226','saleispower',NULL,'W','A','Y',1727848197885,1727848197885,0,0),(137,'7bcc6ebc-778f-4a6c-82c3-45e2f7d737aa','Duplicate','duplicate993@gmail.com','9146808225','saleispower',NULL,'W','A','N',1727853641784,1727856188444,0,0),(140,'76b6942e-e972-40fb-ada5-88962da55742','Retailer','Reatiler993@gmail.com','9145808278','saleispower',NULL,'R','A','N',1727856927502,1727856927502,0,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_groups`
--

DROP TABLE IF EXISTS `user_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_groups` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `group_id` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_groups`
--

LOCK TABLES `user_groups` WRITE;
/*!40000 ALTER TABLE `user_groups` DISABLE KEYS */;
INSERT INTO `user_groups` VALUES (4,44,2),(6,52,2),(25,51,9),(27,1,9),(31,0,0),(35,57,12),(38,53,11),(39,47,11),(41,60,12),(42,65,11),(43,76,12),(44,118,11),(45,122,11),(52,137,11),(53,59,11),(54,140,12);
/*!40000 ALTER TABLE `user_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_types`
--

DROP TABLE IF EXISTS `user_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_types` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_type` varchar(20) DEFAULT NULL,
  `user_enum` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_types`
--

LOCK TABLES `user_types` WRITE;
/*!40000 ALTER TABLE `user_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_types` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-03 20:39:14