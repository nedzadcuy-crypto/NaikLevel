package com.example.data.local

object CurriculumSeed {
    fun getInitialMaterials(): List<MaterialEntity> = listOf(
        // === SD (Sekolah Dasar) ===
        MaterialEntity(
            title = "Pengenalan Pecahan Sederhana",
            description = "Memahami konsep pecahan 1/2, 1/4, 3/4 dengan analogi potongan pizza dan garis bilangan.",
            content = """
                # Pengenalan Pecahan Sederhana
                
                ## 1. Apa itu Pecahan?
                Pecahan adalah bagian dari suatu keseluruhan yang utuh. Bayangkan kamu memiliki satu buah pizza lezat yang dipotong menjadi 4 bagian yang sama besar. Jika kamu memakan 1 potong, maka kamu telah memakan 1 dari 4 bagian, atau ditulis 1/4.
                
                ## 2. Bagian-Bagian Pecahan
                Bentuk pecahan dituliskan sebagai:
                a / b
                - Pembilang (atas, a): Menyatakan berapa banyak bagian yang diambil atau diperhatikan.
                - Penyebut (bawah, b): Menyatakan jumlah total seluruh bagian yang sama besar.
                
                Contoh:
                - Pada 1/2, angka 1 adalah pembilang dan 2 adalah penyebut.
                - Pada 3/4, angka 3 adalah pembilang dan 4 adalah penyebut.
                
                ## 3. Membandingkan Pecahan Sederhana
                Jika penyebutnya sama, kita tinggal melihat pembilangnya:
                - 3/5 lebih besar daripada 1/5 karena 3 potong lebih banyak daripada 1 potong.
                
                ## Ringkasan Konsep
                1. Pecahan = bagian dari benda utuh.
                2. Pembilang di atas, penyebut di bawah.
                3. Selalu pastikan potongan dibagi secara adil dan berukuran sama.
            """.trimIndent(),
            schoolLevel = "SD",
            grade = 4,
            subject = "Matematika",
            topic = "Bilangan & Pecahan",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 4
        ),
        MaterialEntity(
            title = "Daur Hidup Hewan dan Metamorfosis",
            description = "Perbedaan metamorfosis sempurna pada kupu-kupu dan tidak sempurna pada belalang.",
            content = """
                # Daur Hidup Hewan dan Metamorfosis
                
                ## 1. Pengertian Daur Hidup
                Daur hidup hewan adalah tahapan perubahan bentuk tubuh yang dialami hewan sepanjang hidupnya, mulai dari telur hingga dewasa.
                
                ## 2. Metamorfosis Sempurna
                Hewan yang mengalami perubahan bentuk tubuh sangat berbeda di setiap tahapannya.
                Contoh: Kupu-kupu dan Katak
                
                Tahapan kupu-kupu:
                1. Telur
                2. Ulat (Larva): Sangat aktif makan daun.
                3. Kepompong (Pupa): Masa istirahat membentuk struktur tubuh baru.
                4. Kupu-kupu dewasa (Imago): Memiliki sayap indah.
                
                ## 3. Metamorfosis Tidak Sempurna
                Hewan yang bentuk tubuh mudanya mirip dengan hewan dewasa (tanpa fase kepompong).
                Contoh: Belalang dan Kecoa
                
                Tahapan belalang:
                1. Telur
                2. Nimfa (anak belalang tanpa sayap lengkap)
                3. Belalang Dewasa (Imago)
                
                ## Catatan Penting
                Hewan tanpa metamorfosis contohnya kucing, sapi, dan ayam, karena dari lahir bentuknya sudah menyerupai induknya.
            """.trimIndent(),
            schoolLevel = "SD",
            grade = 4,
            subject = "IPA",
            topic = "Makhluk Hidup & Lingkungan",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 5
        ),
        MaterialEntity(
            title = "Menemukan Ide Pokok dalam Paragraf",
            description = "Teknik cepat membaca dan menemukan kalimat utama serta ide pokok teks bacaan.",
            content = """
                # Menemukan Ide Pokok dalam Paragraf
                
                ## 1. Apa itu Ide Pokok?
                Ide pokok (atau gagasan utama) adalah topik inti atau pesan utama yang sedang dibahas dalam sebuah paragraf.
                
                ## 2. Posisi Kalimat Utama
                - Paragraf Deduktif: Kalimat utama terletak di awal paragraf. Diikuti kalimat-kalimat penjelas.
                - Paragraf Induktif: Kalimat utama terletak di akhir paragraf, biasanya diawali kata simpulan (oleh karena itu, dengan demikian).
                - Paragraf Campuran: Kalimat utama di awal dan ditegaskan kembali di akhir.
                
                ## 3. Tips Praktis
                1. Baca seluruh kalimat dengan seksama.
                2. Cari kata kunci yang berulang.
                3. Tanyakan pada diri sendiri: Paragraf ini membicarakan apa?
            """.trimIndent(),
            schoolLevel = "SD",
            grade = 5,
            subject = "Bahasa Indonesia",
            topic = "Literasi & Membaca",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 4
        ),

        // === SMP (Sekolah Menengah Pertama) ===
        MaterialEntity(
            title = "Teorema Pythagoras dan Pembuktian",
            description = "Aturan segitiga siku-siku, rumus a² + b² = c², tripel Pythagoras, dan soal kontekstual.",
            content = """
                # Teorema Pythagoras dan Pembuktian
                
                ## 1. Bunyi Teorema Pythagoras
                Pada segitiga siku-siku, kuadrat sisi miring (hipotenusa) sama dengan jumlah kuadrat dari sisi-sisi penyikunya.
                
                ## 2. Rumus Inti
                c² = a² + b²
                atau
                - c = √(a² + b²)
                - a = √(c² - b²)
                - b = √(c² - a²)
                
                Keterangan:
                - c = sisi miring (sisi terpanjang di hadapan sudut 90 derajat).
                - a, b = sisi tegak siku-siku.
                
                ## 3. Tripel Pythagoras Populer
                Hafalkan tripel ini agar dapat mengerjakan soal dalam hitungan detik:
                - 3, 4, 5 (dan kelipatannya: 6, 8, 10)
                - 5, 12, 13 (dan kelipatannya: 10, 24, 26)
                - 7, 24, 25
                - 8, 15, 17
                
                ## Contoh Soal Kontekstual
                Sebuah tangga bersandar pada dinding tembok. Tinggi dinding yang dicapai tangga adalah 4 meter, dan jarak kaki tangga ke dinding adalah 3 meter. Berapa panjang tangga tersebut?
                Jawab:
                c = √(3² + 4²) = √(9 + 16) = √25 = 5 meter.
            """.trimIndent(),
            schoolLevel = "SMP",
            grade = 8,
            subject = "Matematika",
            topic = "Geometri & Pengukuran",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 6
        ),
        MaterialEntity(
            title = "Sistem Pencernaan Manusia & Enzim",
            description = "Perjalanan makanan dari mulut hingga usus besar, pencernaan mekanik dan kimiawi.",
            content = """
                # Sistem Pencernaan Manusia & Enzim
                
                ## 1. Dua Jenis Pencernaan
                - Mekanik: Mengubah ukuran makanan menjadi lebih kecil tanpa bantuan enzim (misal: gigi di mulut, gerak peristaltik di lambung).
                - Kimiawi: Menguraikan molekul makanan kompleks menjadi molekul sederhana menggunakan enzim.
                
                ## 2. Organ & Enzim Penting
                1. Mulut: Enzim Ptialin (Amilase) menguraikan amilum menjadi maltosa.
                2. Lambung:
                   - Asam Klorida (HCl): Membunuh kuman dan mengaktifkan pepsinogen.
                   - Pepsin: Mengubah protein menjadi pepton.
                   - Renin: Mengendapkan kasein susu.
                3. Usus Halus (Duodenum): Menerima getah pankreas (Amilase, Tripsin, Lipase) dan empedu (mengemulsi lemak).
                4. Usus Besar: Penyerapan air dan pembusukan sisa makanan oleh bakteri E. coli.
                
                ## Tips Belajar
                Ingat jembatan keledai getah pankreas: PLTA (Pankreas: Lipase, Tripsin, Amilase).
            """.trimIndent(),
            schoolLevel = "SMP",
            grade = 8,
            subject = "IPA",
            topic = "Sistem Tubuh Manusia",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 5
        ),
        MaterialEntity(
            title = "Interaksi Antarruang Negara-Negara ASEAN",
            description = "Karakteristik geografis Asia Tenggara, faktor pendorong dan penghambat kerja sama ASEAN.",
            content = """
                # Interaksi Antarruang Negara-Negara ASEAN
                
                ## 1. Latar Belakang ASEAN
                Association of Southeast Asian Nations didirikan pada 8 Agustus 1967 melalui Deklarasi Bangkok oleh 5 negara pendiri:
                - Indonesia (Adam Malik)
                - Malaysia (Tun Abdul Razak)
                - Singapura (S. Rajaratnam)
                - Filipina (Narciso Ramos)
                - Thailand (Thanat Khoman)
                
                ## 2. Faktor Pendorong Kerja Sama
                1. Kesamaan dan perbedaan sumber daya alam.
                2. Kesamaan kondisi geografis kawasan tropis.
                3. Kepentingan menjaga perdamaian dan stabilitas regional.
                
                ## 3. Faktor Penghambat
                1. Perbedaan ideologi.
                2. Konflik dan sengketa batas wilayah.
                3. Kebijakan protektif ekonomi dalam negeri.
            """.trimIndent(),
            schoolLevel = "SMP",
            grade = 9,
            subject = "IPS",
            topic = "Kewilayahan Regional",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 5
        ),

        // === SMA (Sekolah Menengah Atas) ===
        MaterialEntity(
            title = "Persamaan & Fungsi Kuadrat",
            description = "Menentukan akar-akar persamaan kuadrat, rumus kuadratik abc, titik puncak parabola, dan diskriminan.",
            content = """
                # Persamaan & Fungsi Kuadrat
                
                ## 1. Bentuk Umum
                ax² + bx + c = 0, dengan a ≠ 0
                
                ## 2. Metode Mencari Akar-Akar
                1. Pemfaktoran: (x - x1)(x - x2) = 0
                2. Melengkapkan Kuadrat Sempurna
                3. Rumus Kuadratik (ABC):
                x1,2 = (-b ± √(b² - 4ac)) / (2a)
                
                ## 3. Nilai Diskriminan (D = b² - 4ac)
                - D > 0: Memiliki 2 akar real berbeda (kurva memotong sumbu X di 2 titik).
                - D = 0: Memiliki 2 akar kembar / real sama (kurva menyinggung sumbu X).
                - D < 0: Tidak memiliki akar real (kurva tidak memotong sumbu X).
                
                ## 4. Karakteristik Grafik Parabola f(x) = ax² + bx + c
                - Titik puncak: (xp, yp) = (-b / (2a), -D / (4a))
                - Jika a > 0, parabola terbuka ke atas (memiliki nilai minimum).
                - Jika a < 0, parabola terbuka ke bawah (memiliki nilai maksimum).
            """.trimIndent(),
            schoolLevel = "SMA",
            grade = 10,
            subject = "Matematika",
            topic = "Aljabar & Fungsi",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 7
        ),
        MaterialEntity(
            title = "Hukum Newton tentang Gerak",
            description = "Hukum I (Kelembaman), Hukum II (F = m.a), Hukum III (Aksi-Reaksi) beserta aplikasi gaya gesek dan bidang miring.",
            content = """
                # Hukum Newton tentang Gerak
                
                ## 1. Hukum I Newton (Inersia / Kelembaman)
                Setiap benda akan tetap diam atau bergerak lurus beraturan jika resultan gaya yang bekerja padanya sama dengan nol.
                ΣF = 0
                
                ## 2. Hukum II Newton (Dinamika Gerak)
                Percepatan yang dialami suatu benda berbanding lurus dengan resultan gaya dan berbanding terbalik dengan massanya.
                ΣF = m · a
                - Gaya (F) dalam Newton (N).
                - Massa (m) dalam kilogram (kg).
                - Percepatan (a) dalam m/s²
                
                ## 3. Hukum III Newton (Aksi-Reaksi)
                Jika benda A memberikan gaya pada benda B, maka benda B memberikan gaya yang sama besar namun berlawanan arah pada benda A.
                F_aksi = -F_reaksi
                
                Sifat Pasangan Aksi-Reaksi:
                1. Sama besar nilainya.
                2. Berlawanan arah.
                3. Bekerja pada dua benda yang berbeda (tidak saling meniadakan!).
            """.trimIndent(),
            schoolLevel = "SMA",
            grade = 10,
            subject = "IPA",
            topic = "Fisika Mekanika",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 6
        ),
        MaterialEntity(
            title = "Analytical Exposition Text",
            description = "Social function, text structure (Thesis, Arguments, Reiteration), and language features for senior high school English.",
            content = """
                # Analytical Exposition Text
                
                ## 1. Social Function
                The purpose of an analytical exposition is to persuade the reader or listener that something is the case, and that a certain idea or issue is important and needs attention.
                
                ## 2. Generic Structure
                1. Thesis: Introduces the topic and indicates the writer's point of view / stance.
                2. Arguments: Series of arguments supported by evidence, facts, and logical explanations.
                3. Reiteration / Conclusion: Restates the thesis statement and summarizes the main arguments without introducing new claims.
                
                ## 3. Language Features
                - Use of Simple Present Tense (e.g., Education plays a vital role in our future).
                - Use of mental verbs (believe, think, feel).
                - Use of connecting words / causal conjunctions (because, consequently, therefore, furthermore).
                - Evaluative language to express judgment (crucial, alarming, beneficial).
            """.trimIndent(),
            schoolLevel = "SMA",
            grade = 11,
            subject = "Bahasa Inggris",
            topic = "Teks Argumentatif",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 5
        ),

        // === SMK (Sekolah Menengah Kejuruan) ===
        MaterialEntity(
            title = "Dasar Algoritma & Logika Pemrograman",
            description = "Struktur dasar sekuensial, percabangan If-Else, perulangan Loop, dan representasi flowchart.",
            content = """
                # Dasar Algoritma & Logika Pemrograman
                
                ## 1. Definisi Algoritma
                Algoritma adalah urutan langkah logis yang terdefinisi dengan jelas untuk memecahkan suatu masalah komputasi.
                
                ## 2. Tiga Struktur Kontrol Utama
                1. Runtunan (Sequence): Langkah-langkah dieksekusi baris demi baris dari atas ke bawah.
                2. Percabangan (Selection / Branching):
                   - Struktur if-else atau switch-case untuk mengambil keputusan berdasarkan kondisi boolean (true/false).
                3. Perulangan (Iteration / Loop):
                   - for, while, do-while untuk mengeksekusi blok kode berulang kali sampai kondisi terminasi terpenuhi.
                
                ## 3. Komponen Simbol Flowchart Standar
                - Terminator (Oval): Titik awal (Start) atau akhir (End) program.
                - Process (Persegi Panjang): Eksekusi perhitungan atau penugasan variabel.
                - Decision (Belah Ketupat): Titik percabangan kondisi.
                - Input/Output (Jajaran Genjang): Operasi masukan data atau penampilan hasil.
                
                ## Praktik Nyata di Industri
                Di industri perangkat lunak modern, logika dasar yang rapi dan penamaan variabel yang deskriptif adalah kunci kode yang mudah dirawat (clean code).
            """.trimIndent(),
            schoolLevel = "SMK",
            grade = 10,
            subject = "Informatika & RPL",
            topic = "Algoritma Pemrograman",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 6
        ),
        MaterialEntity(
            title = "Arsitektur Jaringan Komputer & Model OSI",
            description = "Tujuh lapisan OSI layer, pengalamatan IPv4, subnetting dasar, dan topologi jaringan LAN.",
            content = """
                # Arsitektur Jaringan Komputer & Model OSI
                
                ## 1. 7 Lapisan OSI Layer
                Untuk mempermudah pemahaman komunikasi jaringan, ISO membuat standar 7 Layer OSI:
                1. Layer 7 - Application: HTTP, HTTPS, FTP, DNS (interaksi pengguna).
                2. Layer 6 - Presentation: Enkripsi, kompresi, format data (SSL, TLS, JPEG).
                3. Layer 5 - Session: Menjaga sesi komunikasi tetap terbuka.
                4. Layer 4 - Transport: Segmentasi data dan kontrol aliran (TCP, UDP).
                5. Layer 3 - Network: Pengalamatan logis dan routing (IP Address, Router).
                6. Layer 2 - Data Link: Pengalamatan fisik (MAC Address, Switch, Frame).
                7. Layer 1 - Physical: Media transmisi kabel UTP, fiber optic, sinyal radio.
                
                ## 2. Jembatan Keledai 7 Layer
                Hafalkan dari bawah ke atas:
                Please Do Not Touch Steve's Pet Alligator
                (Physical, Data Link, Network, Transport, Session, Presentation, Application).
                
                ## 3. Topologi LAN Populer
                - Topologi Star: Menggunakan switch pusat. Jika satu kabel putus, klien lain tidak terganggu.
                - Topologi Tree: Kombinasi star bertingkat untuk skala gedung.
            """.trimIndent(),
            schoolLevel = "SMK",
            grade = 11,
            subject = "Teknik Komputer & Jaringan",
            topic = "Infrastruktur Jaringan",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 6
        ),
        MaterialEntity(
            title = "Pencatatan Transaksi & Siklus Akuntansi",
            description = "Persamaan dasar akuntansi (Aset = Liabilitas + Ekuitas), jurnal umum, buku besar, dan neraca saldo.",
            content = """
                # Pencatatan Transaksi & Siklus Akuntansi
                
                ## 1. Persamaan Dasar Akuntansi
                Aset = Liabilitas (Kewajiban) + Ekuitas (Modal)
                Prinsip penting: Sisi kiri dan sisi kanan harus selalu seimbang (balance).
                
                ## 2. Aturan Debit dan Kredit
                - Aset (Harta): Bertambah di Debit, Berkurang di Kredit.
                - Liabilitas (Utang): Bertambah di Kredit, Berkurang di Debit.
                - Ekuitas (Modal): Bertambah di Kredit, Berkurang di Debit.
                - Pendapatan: Bertambah di Kredit.
                - Beban / Biaya: Bertambah di Debit.
                
                ## 3. Tahapan Siklus Akuntansi
                1. Analisis bukti transaksi (faktur, nota, kuitansi).
                2. Pencatatan ke dalam Jurnal Umum.
                3. Posting ke Buku Besar (Ledger).
                4. Penyusunan Neraca Saldo (Trial Balance).
                5. Jurnal Penyesuaian & Laporan Keuangan (Laba Rugi, Perubahan Modal, Neraca).
            """.trimIndent(),
            schoolLevel = "SMK",
            grade = 11,
            subject = "Bisnis & Akuntansi",
            topic = "Keuangan & Pembukuan",
            source = "KURIKULUM_NASIONAL",
            estimatedReadMinutes = 5
        )
    )

    fun getDefaultQuests(): List<QuestEntity> = listOf(
        QuestEntity(
            id = "daily_read_material",
            title = "Baca 1 Materi Pembelajaran",
            description = "Buka dan pelajari materi kurikulum atau materi buatanmu sampai tuntas.",
            type = "DAILY",
            targetCount = 1,
            currentCount = 0,
            xpReward = 30,
            isClaimed = false,
            periodKey = "daily"
        ),
        QuestEntity(
            id = "daily_complete_exam",
            title = "Selesaikan 1 Simulasi Ujian",
            description = "Uji pemahamanmu dengan mengikuti simulasi soal pilihan ganda berwaktu.",
            type = "DAILY",
            targetCount = 1,
            currentCount = 0,
            xpReward = 50,
            isClaimed = false,
            periodKey = "daily"
        ),
        QuestEntity(
            id = "daily_ask_ai_tutor",
            title = "Tanya Tutor AI NaikLevel",
            description = "Diskusikan pertanyaan atau konsep belajar yang belum kamu pahami dengan AI Tutor.",
            type = "DAILY",
            targetCount = 1,
            currentCount = 0,
            xpReward = 25,
            isClaimed = false,
            periodKey = "daily"
        ),
        QuestEntity(
            id = "weekly_streak_3",
            title = "Pertahankan Belajar 3 Hari",
            description = "Buka aplikasi dan belajar berturut-turut untuk membangun kebiasaan positif.",
            type = "WEEKLY",
            targetCount = 3,
            currentCount = 0,
            xpReward = 100,
            isClaimed = false,
            periodKey = "weekly"
        ),
        QuestEntity(
            id = "weekly_complete_3_materials",
            title = "Kuasai 3 Materi Pelajaran",
            description = "Tandai 3 materi pelajaran sebagai selesai dalam minggu ini.",
            type = "WEEKLY",
            targetCount = 3,
            currentCount = 0,
            xpReward = 120,
            isClaimed = false,
            periodKey = "weekly"
        )
    )
}
