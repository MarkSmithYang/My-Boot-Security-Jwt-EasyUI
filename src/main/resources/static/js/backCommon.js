//注意需要在script标签里写th:inline="none",才能正常解析传递过的指定格式的json数据
$(document).ready(function () {
    $("#dg").datagrid({
        //分页全部使用默认值,懒得设置了
        title: '用户信息表',
        url: '/queryUserList',
        method: 'get',
        fixed: false,
        //在从远程站点加载数据的时候显示提示消息
        loadMsg: '加载中',
        //如果为true，则显示一个行号列
        rownumbers: true,
        fitColumns: true,
        //如果为true，则在DataGrid控件底部显示分页工具栏
        pagination: true,
        pageNumber: 1,
        pageSize: 10,
        //在设置分页属性的时候 初始化页面选择框,最多允许20行,
        //这样好计算全选之后拼接id的长度,接口好做容错判断
        pageList: [10, 20],
        columns: [[
            {field: 'id', title: '', width: '15%', align: 'center', checkbox: true},
            {field: 'username', title: '用户名', width: '15%', align: 'center'},
            {field: 'department', title: '部门', width: '15%', align: 'center'},
            {field: 'position', title: '职位', width: '15%', align: 'center'},
            {field: 'phone', title: '电话', width: '15%', align: 'center'},
            {field: 'from', title: '系统', width: '15%', align: 'center'},
        ]],
        //设置表格行的详情信息
        view: detailview,
        detailFormatter: function (rowIndex, rowData) {
            return "<table style='width:100%;height:100%;border: 2px'><tr>" +
                "<td style='color: red'><b>角色:<b></td>" +
                "<td style='color: greenyellow'><b>" + rowData.role + "<b></td>" +
                "</tr>" +
                "<tr>" +
                "<td style='color: red'><b>模块:<b></td>" +
                "<td style='color: greenyellow'><b>" + rowData.modules + "<b></td>" +
                "</tr>" +
                "<tr>" +
                "<td style='color: red'><b>权限:<b></td>" +
                "<td style='color: greenyellow'><b>" + rowData.permission + "<b></td>" +
                "</tr></table>"
        },
        //设置按钮
        toolbar: [{
            //让边框消失
            plain: true,
            iconCls: 'icon-add',
            text: '新增',
            handler: function () {
                //先清空表数据--实测,如果点击了编辑取消后,点击新增会有数据回显bug(实测)
                reset();
                //隐藏保存按钮
                $("#update").hide();
                //展示想要的按钮,不然会造成按钮消失
                $("#add").show();
                $("#win").window('open');
            }
        }, '-', {
            //让边框消失
            plain: true,
            iconCls: 'icon-edit',
            text: '编辑',
            handler: function () {
                var array = $("#dg").datagrid("getSelections");
                if (array == null || array == undefined || array == '' || array.length == 0) {
                    $.messager.alert('提示消息', "请先选择数据再操作");
                    return;
                }
                //判断是否是多选操作
                if (array.length > 1) {
                    $.messager.alert('提示消息', "请先选择一行数据操作");
                    return;
                }
                //获取这行数据
                var row = array[0];
                //隐藏添加按钮
                $("#add").hide();
                //展示想要的按钮,不然会造成按钮消失
                $("#update").show();
                //打开窗口
                $("#win").window('open');
                //回显被选中的数据到弹框(也是新增框)
                $("#fo").form('load', row);
            }
        }, '-', {
            //让按钮边框消失
            plain: true,
            iconCls: 'icon-cut',
            text: '删除',
            handler: function () {
                //获取被选中的行对象
                var array = $("#dg").datagrid("getSelections");
                if (array == null || array == undefined || array == '' || array.length == 0) {
                    $.messager.alert('提示消息', '请选择数据再操作');
                    return;
                }
                //确认删除
                $.messager.confirm('确认对话框', '您确定要删除吗', function (r) {
                    if (r) {
                        //声明一个变量,用以拼接id
                        var ids = "";
                        for (var i = 0; i < array.length; i++) {
                            ids = ids + array[i].id + ",";
                        }
                        //去除字符串末尾的逗号(,)
                        ids = ids.substring(0, ids.length - 1);
                        //通过ajax来删除数据
                        $.ajax({
                            url: '/deleteUser',
                            method: 'post',
                            dataType: 'json',
                            data: 'ids=' + ids,
                            success: function (data) {
                                if (data.status == 200) {
                                    query();
                                    $.messager.alert('提示消息', '操作成功');
                                } else {
                                    query();
                                    $.messager.alert('错误消息', data.message);
                                }
                            }
                        });
                    }
                });
            }
        }]
    });

//添加用户
    function userAdd() {
        $('#fo').form('submit', {
            url: '/addUser',
            success: function (data) {
                data = JSON.parse(data);
                if (data.status == 200) {
                    //操作成功,去查询列表
                    query();
                    $("#win").window('close');
                    $.messager.alert('提示消息', '操作成功');
                } else {
                    query();
                    $("#win").window('close');
                    $.messager.alert('错误消息', data.message);
                }
            }
        });
    }

//编辑用户
    function userUpdate() {
        //获取选中行
        var array = $("#dg").datagrid("getSelections");
        //只有只选中一行的时候才允许修改
        if (array.length == 1) {
            //表单提交 会获取表单的所有信息
            $('#fo').form('submit', {
                url: 'updateUser/',
                success: function (data) {
                    data = JSON.parse(data);
                    if (data.status == 200) {
                        //调用query()方法查询数据,只要搜索框没有值,就会查询所有
                        query();
                        $("#win").window('close');
                        $.messager.alert('提示消息', '操作成功');
                    } else {
                        query();
                        $("#win").window('close');
                        $.message.alert('错误消息', data.message);
                    }
                }
            });
        } else {
            $.messager.alert('提示消息', '请选择一行进行操作');
        }
    }

//重置表单数据
    function reset() {
        $('#fo').form('reset');
    }

//------------------------------------------------------------------------------------------------

    <!--实现搜索框搜索点击事件和回车键查询,以及清空搜索框后列表恢复-->
//弄了好久,终于可以了,propertychange是专门用来兼容ie的,这个是easyui的写法
//这里就是只要输入框input的值发生变化,就立即触发就能很明确知道输入框的值是不是空
    $(function () {
        $("#search").textbox('textbox').bind("input propertychange", function () {
            var value = $("#search").textbox('getText');
            //判断清空搜索框的时候进行用户名为空的参数的列表查询
            if (value == "" || value == null || value == undefined) {
                $('#dg').datagrid('load', {
                    //easyui所有的方法调用-->控件对象.控件名称('方法名','方法参数')
                    //http://localhost:8888/queryUserList?username=xxx&page=1&rows=10
                    //这里设置这个相当于拼接了一个参数去请求查询列表的接口
                    username: ''
                });
            }
        })
    });

//全局判断按键(按回车)-->通过回车键也能触发搜索操作
    $(document).keypress(function (e) {
        var eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
        //回车键操作
        if (eCode == 13) {
            //调用搜索方法搜索
            query();
        }
    });

//通过用户名查询用户数据
    function query() {
        //使用easyUI的方法获取值
        var value = $("#search").textbox('getText');
        //判断搜索框的值是否为空
        if (value == "" || value == null || value == undefined) {
            //通过这个可以请
            $('#dg').datagrid('load', {
                //easyui所有的方法调用-->控件对象.控件名称('方法名','方法参数')
                //http://localhost:8888/queryUserList?username=xxx&page=1&rows=10
                //这里设置这个相当于拼接了一个参数去请求查询列表的接口
                username: ''
            });
        } else {
            //解决后台获取数据乱码问题
            var username = encodeURI(value);
            //通过这个可以请
            $('#dg').datagrid('load', {
                //easyui所有的方法调用-->控件对象.控件名称('方法名','方法参数')
                //http://localhost:8888/queryUserList?username=xxx&page=1&rows=10
                //这里设置这个相当于拼接了一个参数去请求查询列表的接口
                username: username
            });
        }
    }

    //-----------------------------------------------------------
    //树形菜单处理
    //处理tree的js代码
    $("#tree").tree({
        url: '/findModules',
        method: 'get',
        //定义节点在展开或折叠的时候是否显示动画效果
        animate: true,
        //checkbox: true,
        //在用户点击一个节点的时候触发
        onClick: function (node) {
            $("#tree").tree('expand', node.target);
        },
        loadFilter: function (data) {
            if (data.status == 200) {
                return data.data;
            } else {
                return null;
            }
        }
    });


});

