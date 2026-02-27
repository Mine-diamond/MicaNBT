package tech.minediamond.micanbt.path;

import org.junit.jupiter.api.Test;
import tech.minediamond.micanbt.path.nbtpathtoken.*;
import tech.minediamond.micanbt.snbt.SNBT;
import tech.minediamond.micanbt.tag.*;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class NBTPathTest {

    public static Tag tag = initTag();
    public static Tag oldVersionTag = getOldVersionTag();
    public static Tag newVersionTag = getNewVersionTag();

    @Test
    public void testPath() {
        assertPath("\"id\":\"minecraft:black_shulker_box\"", "id");
        assertPath("\"Name\":\"{\\\"text\\\":\\\"维度递归核心\\\",\\\"color\\\":\\\"gold\\\",\\\"italic\\\":false}\"", "tag.display.Name");
        assertPath("\"\":{id:\"minecraft:protection\",lvl:10s}", "tag.Enchantments[0]");
        assertPath("\"\":{id:\"minecraft:unbreaking\",lvl:10s}", "tag.Enchantments[-1]");
        assertPath("\"Frequency.in.last.hour\":440.0d", "tag.CustomData.RootLayer.Level1.Level2.Level3.Parameters.\"Frequency.in.last.hour\"");
        assertPath("\"\":1", "tag.CustomData.RootLayer.Level1.Level2.Level3.Parameters.Level4.Security.Level5.Matrix[0][0]");

        assertPath("\"id\":\"minecraft:written_book\"", "tag.BlockEntityTag.Items[0]{Slot: 13b, Count: 1b}.id");
        assertPath("\"\":\"{\\\"text\\\":\\\"状态: 活跃\\\",\\\"color\\\":\\\"dark_purple\\\"}\"", "tag.display{Name: '{\"text\":\"维度递归核心\",\"color\":\"gold\",\"italic\":false}'}.Lore[1]");
        assertPath("\"\":10b", "tag.CustomData.RootLayer.Level1.Data[10b]");
        assertPath("\"\":723", "tag.CustomData.RootLayer.Level1{Data: [10b, 20b, 30b]}.Level2.Level3.tick[723i]");

        assertRawToken("\"\":{id:\"minecraft:unbreaking\",lvl:10s}", new KeyToken("tag"), new KeyToken("Enchantments"), new IndexToken(1));
        assertRawToken("\"Slot\":13b", new KeyToken("tag"), new KeyToken("BlockEntityTag"), new KeyToken("Items"), new IndexToken(0), new KeyToken("Slot"));
        CommonCompoundTag filterCompound = new CommonCompoundTag("");
        filterCompound.put(new ListTag<>("Data", List.of(new ByteTag("", (byte) 10), new ByteTag("", (byte) 20), new ByteTag("", (byte) 30))));
        assertRawToken("\"\":723",
                new KeyToken("tag"),
                new KeyToken("CustomData"),
                new KeyToken("RootLayer"),
                new KeyToken("Level1"),
                new FilterToken(filterCompound),
                new KeyToken("Level2"),
                new KeyToken("Level3"),
                new KeyToken("tick"),
                new MatchToken(new IntTag("", 723)));

        assertPathNull("tag.tagNotExist");

        assertToString("id", "id");
        assertToString("tag.sub_tag", "tag.sub_tag");
        assertToString("tag.sub_tag", "tag.\"sub_tag\"");
        assertToString("tag.list[2].id", "tag.list[2].id");
        assertToString("tag.\"sub.tag\"", "tag.\"sub.tag\"");

        assertPath("\"Name\":\"{\\\"text\\\":\\\"维度递归核心\\\",\\\"color\\\":\\\"gold\\\",\\\"italic\\\":false}\"", NBTPath.of("tag.display").resolve("Name"));
        assertPath("\"\":{id:\"minecraft:protection\",lvl:10s}", NBTPath.of("tag.Enchantments").resolve(NBTPath.of("[0]")));

        assertAtAny("posX", "playerPos.X");
        assertAtAny("oldName", "newName");
    }

    public void assertPath(String expected, String path) {
        assertEquals(expected, tag.at(path).toString());
    }

    public void assertPath(String expected, NBTPath path) {
        assertEquals(expected, tag.at(path).toString());
    }

    public void assertRawToken(String expected, PathToken... path) {
        assertEquals(expected, Objects.requireNonNull(NBTFinder.get(tag, NBTPath.fromParts(path))).toString());
    }

    public void assertPathNull(String path) {
        assertNull(tag.at(path));
    }

    public void assertToString(String expected, String path) {
        assertEquals(expected, NBTPath.of(path).toString());
    }

    public void assertAtAny(String... paths) {
        assertNotNull(oldVersionTag.atAny(paths));
        assertNotNull(newVersionTag.atAny(paths));
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
                                            tick: [354, 723],
                                            Parameters: {
                                                Frequency.in.last.hour: 440.0d,
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

    public static Tag getOldVersionTag() {
        String snbtData = """
                {
                    posX: 123,
                    posY: 66,
                    posZ: 1233,
                    oldName: "str"
                }
                """;

        return SNBT.parse(snbtData);
    }

    public static Tag getNewVersionTag() {
        String snbtData = """
                {
                    "playerPos": {
                        X: 123,
                        Y: 66,
                        Z: 1233,
                    },
                    newName: "str"
                }
                """;
        return SNBT.parse(snbtData);
    }
}
