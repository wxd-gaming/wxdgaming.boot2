package wxdgaming.boot2.starter.excel;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 单元格信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 20:46
 **/
@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CellInfo {

    /** 格子id */
    private int cellIndex;
    /** 表格配置的类型 */
    private String cellType;
    /** 归属 */
    private String fieldBelong;
    /** 名字 */
    private String fieldName;
    /** 类型 */
    private Class<?> fieldType;
    /** 类型 */
    private String fieldTypeString;
    /** 长度 */
    private int size;
    /** 备注 */
    private String fieldComment;

}
