/* 分页组件js文件 */

class PageView {

    url;
    gameId = 0;
    pathname = null;
    columnNames = [];
    columnComments = [];
    items = [];
    dataCount = 0;
    rowFunction = null;
    searchFunction = null;

    constructor(url, searchFunction, rowFunction) {
        this.pathname = window.location.protocol + "//" + window.location.host + "" + window.location.pathname;
        this.url = url;
        this.searchFunction = searchFunction;
        this.rowFunction = rowFunction;

        let html = `
<div style="position: absolute;left: 2px;right: 2px;bottom: 5px;padding: 2px;border-radius: 0px;overflow: auto;text-align: center;">
    <div>
        <label for="page_size">共</label>
        <label id="lab_row_count" style="width: 40px; text-align: center; border: slategrey 1px solid;background-color: white;">0</label>
        &nbsp;&nbsp;
        <label for="page_size">每页显示</label>
        <select id="page_size" onchange="pageView.nextPage(-99999999999999999999)">
            <option value="20">20</option>
            <option value="30" selected="selected">30</option>
            <option value="40">40</option>
            <option value="50">50</option>
            <option value="100">100</option>
            <option value="500">500</option>
        </select>
        <label for="page_size">条</label>
        <a href="javascript:void(0);" onclick="pageView.nextPage(-99999999999999999999)">首页</a>
        <a href="javascript:void(0);" onclick="pageView.nextPage(-1)">上一页</a>
        &nbsp;&nbsp;
        <label id="lab_page_index" style="width: 40px; text-align: center; border: slategrey 1px solid;background-color: white;">1</label>
        /
        <label id="lab_page_max" style="width: 40px; text-align: center; border: slategrey 1px solid;background-color: white;">1</label>
        &nbsp;&nbsp;
        <a href="javascript:void(0);" onclick="pageView.nextPage(1)">下一页</a>
        <a href="javascript:void(0);" onclick="pageView.nextPage(99999999999999999999)">末页</a>
    </div>
</div>
        `;

        $(document.body).append(html);

        /*读取本地存储，根据个人爱好查看数据*/
        let ps = localStorage.getItem(this.pathname + "-page-max");
        if (!wxd.isNull(ps)) {
            $('#page_size').val(ps);
        }

    }

    remoteGetData(postQuery) {
        postQuery.put("pageIndex", this.pageIndex());
        postQuery.put("pageSize", this.pageSize());
        wxd.netty.post(this.url, postQuery.toString(),
            (responseText) => {
                if (responseText.code !== 1) {
                    wxd.message.alert("异常：" + responseText.msg);
                    return
                }
                this.items = responseText.data;
                this.dataCount = Number(responseText.rowCount);
                $("#lab_row_count").text(responseText.rowCount);
                this.pageMaxIndex();
                this.showData();
            },
            (errorMsg) => {
                wxd.message.alert("异常：" + errorMsg);
            },
            true,
            30_000
        );
    }

    pageSize() {
        return Number($('#page_size').val());
    }

    pageIndex() {
        return Number($("#lab_page_index").text());
    }

    pageMaxIndex() {
        let limit = this.pageSize();
        let number = this.dataCount % limit === 0 ? this.dataCount / limit : Math.floor(this.dataCount / limit) + 1;
        $("#lab_page_max").text(number);
        return number;
    }

    nextPage(change) {
        let oldPageIndex = this.pageIndex();
        let index = oldPageIndex + change;
        if (index < 1) {
            index = 1;
        }
        let maxIndex = this.pageMaxIndex();
        if (index > maxIndex) {
            index = maxIndex;
        }

        localStorage.setItem(this.pathname + "-page-max", this.pageSize().toString());

        $("#lab_page_index").text(index);

        if (change > -100 && oldPageIndex === index) return
        this.searchFunction();
    }

    async showData() {
        $("tbody:first").html("");
        let skip = Number($("#lab_page_index").text()) - 1;
        if (skip < 0)
            skip = 0;
        let limit = this.pageSize();
        skip = skip * limit;
        await wxd.delayed((index) => {
            if (index >= this.items.length) {
                return;
            }
            $("tbody:first").append(this.rowFunction(skip + index + 1, this.items[index]));
        }, 10, limit)
        wxd.message.tips_init_bind(); //初始化
    }

}
