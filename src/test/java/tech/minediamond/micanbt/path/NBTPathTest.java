package tech.minediamond.micanbt.path;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.SNBT.SNBT;
import tech.minediamond.micanbt.tag.Tag;

import static org.junit.jupiter.api.Assertions.*;

public class NBTPathTest {

    public static Tag tag = initTag();

    @Test
    public void testPath() {
        assertPath("\"id\":\"minecraft:black_shulker_box\"", "id");
        assertPath("\"Name\":\"{\\\"text\\\":\\\"维度递归核心\\\",\\\"color\\\":\\\"gold\\\",\\\"italic\\\":false}\"", "tag/display/Name");
        assertPath("\"\":{id:\"minecraft:protection\",lvl:10s}","tag/Enchantments/0");
        assertPath("\"\":1", "tag/CustomData/RootLayer/Level1/Level2/Level3/Parameters/Level4/Security/Level5/Matrix/0/0");
        assertPathNull("tag/tagNotExist");
    }

    public void assertPath(String expected, String path) {
        assertEquals(expected, tag.at(path).toString());
    }

    public void assertPathNull(String path) {
        assertNull(tag.at(path));
    }

    public static Tag initTag() {
        String snbtData = """
                {
                    id: "minecraft:black_shulker_box",
                    Count: 1b,
                    tag: {
                        display: {
                            Name: '{"text":"维度递归核心","color":"gold","italic":false}',
                            Lore: [
                                '{"text":"包含层级: 256","color":"gray"}',
                                '{"text":"状态: 活跃","color":"dark_purple"}'
                            ]
                        },
                        Enchantments: [
                            {id: "minecraft:protection", lvl: 10s},
                            {id: "minecraft:unbreaking", lvl: 10s}
                        ],
                        CustomData: {
                            RootLayer: {
                                Level1: {
                                    Data: [10b, 20b, 30b],
                                    Level2: {
                                        Identifier: "uuid-000-111-222",
                                        Level3: {
                                            IsActive: 1b,
                                            Parameters: {
                                                Frequency: 440.0d,
                                                Velocity: {x: 0.5f, y: 1.2f, z: -0.1f},
                                                Level4: {
                                                    DeepStorage: [
                                                        {Item: "minecraft:diamond", Count: 64b, Meta: {Purity: 100s}},
                                                        {Item: "minecraft:netherite_ingot", Count: 16b, Meta: {Origin: "Void"}}
                                                    ],
                                                    Security: {
                                                        Key: "A-99-X",
                                                        Level5: {
                                                            LogicGate: "OR",
                                                            Matrix: [[1, 0], [0, 1]]
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        BlockEntityTag: {
                            Items: [
                                {
                                    Slot: 13b,
                                    id: "minecraft:written_book",
                                    Count: 1b,
                                    tag: {
                                        title: "递归日志",
                                        author: "Unknown",
                                        pages: [
                                            '{"text":"第一页：一切都始于深渊。"}'
                                        ]
                                    }
                                }
                            ]
                        }
                    }
                }
                
                """;


        return SNBT.parse(snbtData);
    }
}
