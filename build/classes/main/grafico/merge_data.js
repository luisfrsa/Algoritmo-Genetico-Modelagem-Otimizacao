for (var a = 1; a <= 9; a++) {
    window["data_grafico_caso" + a] = [];
    var geneticData = window["geneticData_caso" + a];
//    console.log(geneticData);
    var maior_len = 0;
    var ind_maior_len = 0;
    window["labels_caso" + a] = ["Tempo"];
    for (var i in geneticData) {
        window["labels_caso" + a].push(geneticData[i].shift().toString());
        if (geneticData[i].length > maior_len) {
            maior_len = geneticData[i].length;
            ind_maior_len = i - 1;
        }
    }
//    console.log(labels);
    var len_geneticData = geneticData.length;
    var arrayind;
    for (var i in geneticData[ind_maior_len]) {
        arrayind = [parseInt(i)];
        for (var j = 0; j < len_geneticData; j++) {
            if (typeof (geneticData[j][i]) === 'undefined' || typeof (geneticData[j][i][1]) === 'undefined') {
                geneticData[j][i] = [null, geneticData[j][i - 1][1]];
            }
            arrayind.push(geneticData[j][i][1]);
        }
        window["data_grafico_caso" + a].push(arrayind);
    }
//    console.log(window["data_grafico_caso" + a]);
}