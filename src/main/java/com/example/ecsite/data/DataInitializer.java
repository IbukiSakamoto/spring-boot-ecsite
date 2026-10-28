package com.example.ecsite.data;

import com.example.ecsite.model.Product;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            List<Product> sampleProducts = List.of(
                    new Product("高性能ノートPC", "高速SSDと最新プロセッサを搭載した開発・ビジネス向けラップトップPCです。", 128000, 5),
                    new Product("ノイズキャンセリングヘッドホン", "没入感のある高音質と快適な装着感を提供するワイヤレスヘッドホン。", 24800, 10),
                    new Product("メカニカルキーボード", "心地よい打鍵感と耐久性を兼ね備えた静音赤軸キーボード。", 15800, 8),
                    new Product("エルゴノミクスマウス", "手首の負担を軽減する人間工学設計の高精度ワイヤレスマウス。", 6980, 12),
                    new Product("4Kディスプレイ 27インチ", "高精細なIPSパネルを採用した作業効率アップに最適な外部モニター。", 42000, 4),
                    new Product("Java入門実践ガイド (書籍)", "基礎からSpring BootによるWebアプリ構築まで網羅した技術書。", 3500, 20),
                    new Product("スマートウォッチ", "健康管理から通知確認までこなす防水仕様の最新スマートウォッチ。", 19800, 7),
                    new Product("全自動ドリップコーヒーメーカー", "毎朝挽きたての香り高いコーヒーを楽しめるコンパクトなコーヒーマシン。", 9800, 3)
            );
            productRepository.saveAll(sampleProducts);
        }
    }
}
