-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: barbershop
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` varchar(30) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgex1lmaqpg0ir5g1f5eftyaa1` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'ROLE_ADMIN','admin','123'),(2,'ROLE_USER','user1','123');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ca_lam`
--

DROP TABLE IF EXISTS `ca_lam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ca_lam` (
  `gio_bat_dau` time(6) DEFAULT NULL,
  `gio_ket_thuc` time(6) DEFAULT NULL,
  `ma_ca` int NOT NULL,
  `ngay_lam_viec` date DEFAULT NULL,
  PRIMARY KEY (`ma_ca`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ca_lam`
--

LOCK TABLES `ca_lam` WRITE;
/*!40000 ALTER TABLE `ca_lam` DISABLE KEYS */;
INSERT INTO `ca_lam` VALUES ('08:00:00.000000','12:00:00.000000',1,'2025-11-25');
/*!40000 ALTER TABLE `ca_lam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dich_vu`
--

DROP TABLE IF EXISTS `dich_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dich_vu` (
  `gia` double DEFAULT NULL,
  `ma_dv` int NOT NULL AUTO_INCREMENT,
  `thoi_gian_thuc_hien` int DEFAULT NULL,
  `ten_dv` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ma_dv`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dich_vu`
--

LOCK TABLES `dich_vu` WRITE;
/*!40000 ALTER TABLE `dich_vu` DISABLE KEYS */;
INSERT INTO `dich_vu` VALUES (50000,1,20,'Cắt tóc'),(10000,2,5,'Gội đầu');
/*!40000 ALTER TABLE `dich_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hoa_don`
--

DROP TABLE IF EXISTS `hoa_don`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hoa_don` (
  `ma_hd` int NOT NULL AUTO_INCREMENT,
  `ma_lh` int DEFAULT NULL,
  `ngay_thanh_toan` date DEFAULT NULL,
  `tong_tien` double DEFAULT NULL,
  `phuong_thuc_tt` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ma_hd`),
  KEY `FKghitgjshmo9mw51p7a05eh0cb` (`ma_lh`),
  CONSTRAINT `FKghitgjshmo9mw51p7a05eh0cb` FOREIGN KEY (`ma_lh`) REFERENCES `lich_hen` (`ma_lh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hoa_don`
--

LOCK TABLES `hoa_don` WRITE;
/*!40000 ALTER TABLE `hoa_don` DISABLE KEYS */;
/*!40000 ALTER TABLE `hoa_don` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `khach_hang`
--

DROP TABLE IF EXISTS `khach_hang`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khach_hang` (
  `account_id` int DEFAULT NULL,
  `makh` int NOT NULL AUTO_INCREMENT,
  `ngay_sinh` date DEFAULT NULL,
  `gioi_tinh` varchar(10) DEFAULT NULL,
  `sdt` varchar(20) DEFAULT NULL,
  `ho_ten` varchar(100) NOT NULL,
  PRIMARY KEY (`makh`),
  UNIQUE KEY `UKaltrjwb4si5pi5noki9m4luou` (`account_id`),
  CONSTRAINT `FKhmkyfp115c2sjj4gjab9ciyqd` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach_hang`
--

LOCK TABLES `khach_hang` WRITE;
/*!40000 ALTER TABLE `khach_hang` DISABLE KEYS */;
INSERT INTO `khach_hang` VALUES (2,1,'2010-01-25','Nam','0000000001','Nguyễn Văn A');
/*!40000 ALTER TABLE `khach_hang` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lich_hen`
--

DROP TABLE IF EXISTS `lich_hen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lich_hen` (
  `gio_hen` time(6) DEFAULT NULL,
  `ma_lh` int NOT NULL AUTO_INCREMENT,
  `makh` int DEFAULT NULL,
  `manv` int DEFAULT NULL,
  `ngay_hen` date DEFAULT NULL,
  PRIMARY KEY (`ma_lh`),
  KEY `FKj06c38dl86bwtgu21hsi8maaf` (`makh`),
  KEY `FK8hyln9erg35fc58032qrn9kg0` (`manv`),
  CONSTRAINT `FK8hyln9erg35fc58032qrn9kg0` FOREIGN KEY (`manv`) REFERENCES `nhan_vien` (`manv`),
  CONSTRAINT `FKj06c38dl86bwtgu21hsi8maaf` FOREIGN KEY (`makh`) REFERENCES `khach_hang` (`makh`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lich_hen`
--

LOCK TABLES `lich_hen` WRITE;
/*!40000 ALTER TABLE `lich_hen` DISABLE KEYS */;
INSERT INTO `lich_hen` VALUES ('09:00:00.000000',2,1,1,'2025-11-25'),('10:00:00.000000',3,1,1,'2025-11-25'),('10:20:00.000000',4,1,1,'2025-11-25');
/*!40000 ALTER TABLE `lich_hen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lich_hen_dich_vu`
--

DROP TABLE IF EXISTS `lich_hen_dich_vu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lich_hen_dich_vu` (
  `ma_dv` int NOT NULL,
  `ma_lh` int NOT NULL,
  `ghi_chu` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ma_dv`,`ma_lh`),
  KEY `FK11ht0wcje92yj63kn9h331xw4` (`ma_lh`),
  CONSTRAINT `FK11ht0wcje92yj63kn9h331xw4` FOREIGN KEY (`ma_lh`) REFERENCES `lich_hen` (`ma_lh`),
  CONSTRAINT `FKqpqtwhnn83s1pjox2hko1lce` FOREIGN KEY (`ma_dv`) REFERENCES `dich_vu` (`ma_dv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lich_hen_dich_vu`
--

LOCK TABLES `lich_hen_dich_vu` WRITE;
/*!40000 ALTER TABLE `lich_hen_dich_vu` DISABLE KEYS */;
INSERT INTO `lich_hen_dich_vu` VALUES (1,2,NULL),(1,3,NULL),(1,4,NULL);
/*!40000 ALTER TABLE `lich_hen_dich_vu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nhan_vien`
--

DROP TABLE IF EXISTS `nhan_vien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nhan_vien` (
  `luong_co_ban` double DEFAULT NULL,
  `ma_ca` int DEFAULT NULL,
  `manv` int NOT NULL AUTO_INCREMENT,
  `ngay_sinh` date DEFAULT NULL,
  `ngay_vao_lam` date DEFAULT NULL,
  `gioi_tinh` varchar(10) DEFAULT NULL,
  `sdt` varchar(20) DEFAULT NULL,
  `chuc_vu` varchar(50) DEFAULT NULL,
  `ho_ten` varchar(100) DEFAULT NULL,

  `account_id` int DEFAULT NULL,

  PRIMARY KEY (`manv`),
  UNIQUE KEY `UK_nhanvien_account` (`account_id`),
  KEY `FKfa850k7guf8q6rfs8l6ky1plt` (`ma_ca`),
  CONSTRAINT `FKfa850k7guf8q6rfs8l6ky1plt` FOREIGN KEY (`ma_ca`) REFERENCES `ca_lam` (`ma_ca`),
  CONSTRAINT `FK_nhanvien_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nhan_vien`
--

LOCK TABLES `nhan_vien` WRITE;
/*!40000 ALTER TABLE `nhan_vien` DISABLE KEYS */;
INSERT INTO `nhan_vien` VALUES (10000000,1,1,'1999-01-01','2025-11-24','Nam','0000000001','Thợ cắt tóc','Nguyễn Văn A');
/*!40000 ALTER TABLE `nhan_vien` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-25  9:19:03
