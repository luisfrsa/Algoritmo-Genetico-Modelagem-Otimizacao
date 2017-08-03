window.onload = function () {
    for (var a = 1; a <= 9; a++) {
        var data_grafico = window["data_grafico_caso" + a];
//        console.log(data_grafico);
        if (data_grafico.length > 0) {
            g = new Dygraph(
                    document.getElementById("graphdiv_caso" + a),
                    data_grafico,
                    {
                        labels: window["labels_caso" + a],
                        strokeWidth: 3,
                        colors: ["#33cccc", "#ed0000", "#1a61db", "#b200ff", "#1adb7e", "#30db1a", "#9edb1a", "#1abedb", "#db9a1a", "#db401a", "#20228c", "#55c215", "#ef173b"],
                    }
            );
        }
    }

    var button = document.getElementsByTagName('button');
    for (var i = 0; i < button.length; i++) {
        button[i].onclick = function () {
            var charts = document.getElementsByTagName('box_grafico');
            for (var j = 0; j < charts.length; j++) {
                charts[j].style.display = 'none';
            }
            console.log(this.dataset.id);
            document.getElementById(this.dataset.id).style.display = 'block';
        };
    }

}