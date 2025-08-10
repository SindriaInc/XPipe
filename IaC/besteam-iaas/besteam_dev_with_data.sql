-- MySQL dump 10.13  Distrib 8.0.37, for Linux (x86_64)
--
-- Host: besteam-userdb-dev-mysql.cz2weu486xpb.eu-west-1.rds.amazonaws.com    Database: userdb
-- ------------------------------------------------------
-- Server version	8.0.40

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '';

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` char(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID primary key',
  `wallet_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Unique wallet identifier',
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User email address',
  `nickname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User display name',
  `check_newsletter` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Newsletter subscription flag',
  `accepted_privacy` tinyint(1) NOT NULL COMMENT 'Privacy policy acceptance flag',
  `nationality` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User nationality',
  `player_location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Player location/region',
  `date_of_birth` date DEFAULT NULL COMMENT 'User date of birth',
  `gender` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'User gender',
  `favourite_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Preferred game role',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'Account enabled flag',
  `is_verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'Account verification status',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Record update timestamp',
  `game_wallet` decimal(19,2) DEFAULT NULL COMMENT 'In-game wallet balance',
  `actual_role` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Current game role',
  `avatar_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Avatar identifier',
  `registration_state` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Registration process state',
  PRIMARY KEY (`id`),
  UNIQUE KEY `wallet_code` (`wallet_code`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_wallet_code` (`wallet_code`),
  KEY `idx_users_enabled` (`enabled`),
  KEY `idx_users_is_verified` (`is_verified`),
  KEY `idx_users_created_at` (`created_at`),
  KEY `idx_users_nationality` (`nationality`),
  KEY `idx_users_player_location` (`player_location`),
  KEY `idx_users_registration_state` (`registration_state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User accounts table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('62897df6-5510-11f0-bac3-06ac8472984f','TEST_DEV_USER','dev.test@besteam.com','DevTestUser',0,1,NULL,NULL,NULL,NULL,NULL,1,0,'2025-06-29 17:42:10','2025-06-29 17:42:10',NULL,NULL,NULL,NULL),('cac6253f-550d-11f0-bac3-06ac8472984f','WALLET_TEST_001','test@besteam.com','TestUser',0,1,'Ireland','Dublin',NULL,NULL,'Support',1,0,'2025-06-29 17:23:37','2025-06-29 17:28:59',1000.50,'Healer',NULL,'completed'),('cac62cd0-550d-11f0-bac3-06ac8472984f','WALLET_TEST_002','admin@besteam.com','AdminUser',0,1,'Ireland','Cork',NULL,NULL,'Tank',1,0,'2025-06-29 17:23:37','2025-06-29 17:28:38',2500.75,NULL,NULL,'completed');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-10  5:24:08
