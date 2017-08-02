var data_grafico = [];
//var geneticData = [];
//geneticData.push([[0, 123], [1, 13122], [2, 423]]);
//geneticData.push([[0, 123], [1, 13122], [2, 423], [3, 4423]]);
var maior_len = 0;
var ind_maior_len = 0;
var labels = ["Tempo"];
for (var i in geneticData) {
    labels.push(geneticData[i].shift().toString());
    if (geneticData[i].length > maior_len) {
        maior_len = geneticData[i].length;
        ind_maior_len = i - 1;
    }
}
console.log(labels);
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
    data_grafico.push(arrayind);
}
console.log(data_grafico);