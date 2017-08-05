<?php

echo "<pre>";
$dirLog = "log/";
$scan = scandir($dirLog);
foreach ($scan as $dir) {
    if ($dir != '..' and $dir != '.') {
        $content = file_get_contents($dirLog . $dir);
        if (strstr($content, '.::Melhor solucao::.')) {
            $indexSoma = 0;
            if (strstr($content, 'SEM BUSCA NA VIZINHANCA')) {
                $indexSoma = 1;
            }
            $explodeLinha = explode("\n", $content);
            $meta = getMeta($explodeLinha[0], $explodeLinha[1]);
            for ($i = (12 + $indexSoma); $i < (count($explodeLinha) - 4); $i++) {
                $data[] = getData($explodeLinha[$i]);
            }
            $data[] = getBest($explodeLinha[count($explodeLinha) - 2], $data[count($data) - 1]['indice']);
            print_r($meta);
            echo toJs($data, $meta);
            print_r($explodeLinha);
            break;
        }
    }
}

function toJs($data, $meta) {
    $strRet = "var $meta[var] = [[$meta[time]],";
    foreach ($data as $val) {
        $strRet.="[$val[indice],$val[valor]],";
    }
    $strRet = substr($strRet, 0, -1) . "];";
    $strRet.= PHP_EOL . $meta[var_case] . "push($meta[var]);" . PHP_EOL;
    return $strRet;
}

function getMeta($line1, $line2) {
    $explodeName = explode(" ", $line1);
    $explodeCase = explode(" ", $line2);
    $return['name'] = $explodeName[count($explodeName) - 1];
    $return['time'] = substr($explodeName[count($explodeName) - 1], 4, -5);
    $return['var'] = "geneticData_" . substr($explodeName[count($explodeName) - 1], 4, -5);
    $return['case'] = "data_" . substr($explodeCase[count($explodeCase) - 1], 0, -4) . "js";
    $return['var_case'] = "geneticData_" . substr($explodeCase[count($explodeCase) - 1], 0, -4);
    return $return;
}

function getData($line) {
    $explodeSpace = explode(" ", $line);
    $return['indice'] = $explodeSpace[0];
    $return['valor'] = $explodeSpace[5];
    return $return;
}

function getBest($line, $index) {
    $explodeSpace = explode(" ", $line);
    return array("indice" => ($index + 1), 'valor' => $explodeSpace[2]);
}

?>