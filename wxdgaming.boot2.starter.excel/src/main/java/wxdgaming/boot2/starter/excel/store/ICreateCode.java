package wxdgaming.boot2.starter.excel.store;


import wxdgaming.boot2.starter.excel.TableData;

public interface ICreateCode {

    void createCode(TableData tableData, String outPath, String packageName, String belong);

}
