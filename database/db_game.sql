-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 20 Jun 2025 pada 12.44
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_game`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `thasil`
--

CREATE TABLE `thasil` (
  `username` varchar(255) NOT NULL,
  `skor` int(11) DEFAULT 0,
  `count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `thasil`
--

INSERT INTO `thasil` (`username`, `skor`, `count`) VALUES
('apuy', 521, 12),
('azzam_zam_zam', 761, 16),
('bit123', 1320, 22),
('ellen', 867, 17),
('gasca', 980, 18),
('GlyphHunter', 760, 15),
('JAGOAN', 1550, 25),
('jesya', 878, 15),
('juleee', 1840, 25),
('kai', 1260, 22),
('MageBlade', 540, 11),
('master_collector', 650, 13),
('nbening', 1482, 21),
('NewbieWizard', 680, 13),
('puri', 1504, 20),
('rieski', 502, 6),
('yayat', 1429, 24);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `thasil`
--
ALTER TABLE `thasil`
  ADD PRIMARY KEY (`username`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
