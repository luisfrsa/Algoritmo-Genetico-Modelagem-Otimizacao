window.onload = function () {
    g = new Dygraph(
            document.getElementById("graphdiv"),
            data_grafico,
            {
                labels: labels,
                strokeWidth: 3,
                colors: ["#33cccc","#ed0000","#1a61db","#b200ff","#1adb7e","#30db1a","#9edb1a","#1abedb","#db9a1a","#db401a","#20228c","#55c215","#ef173b"],
            }
    );
}