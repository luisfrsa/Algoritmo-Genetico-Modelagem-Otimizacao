<?php

echo "<pre>";
$dirLog = "log/";
$scan = scandir($dirLog);
foreach ($scan as $dir) {
    if ($dir != '..' and $dir != '.') {
        $content = file_get_contents($dirLog . $dir);
        $sv = 0;
        if (strstr($content, 'SEM BUSCA NA VIZINHANCA')) {
            $sv = 1;
        }
        $explodeLinha = explode("\n", $content);
        $meta = getMeta($explodeLinha[0], $explodeLinha[1], $sv);
        if (strstr($content, '.::Melhor solucao::.')) {
            $data = array();
            for ($i = (12 + $sv); $i < (count($explodeLinha) - 4); $i++) {
                $data[] = getData($explodeLinha[$i]);
            }
            $data[] = getBest($explodeLinha[count($explodeLinha) - 2], $data[count($data) - 1]['indice']);
            $str = toJs($data, $meta);
            writeData($str, $meta);
        } else if (strstr($content, 'Tamanho->')) {
            $data = array();
            for ($i = (12 + $sv); $i < (count($explodeLinha) - 1); $i++) {
                $data[] = getData($explodeLinha[$i]);
            }
            $str = toJs($data, $meta);
            writeData($str, $meta);
        } else {
            echo "Sem melhor solucao" . $dir;
        }
    }
}

function writeData($str, $meta) {
    $file = "../main/grafico/data/" . $meta['case'];
    $handle = fopen($file, "a+");
    fwrite($handle, $str);
    fclose($handle);
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

function getMeta($line1, $line2, $sv) {
    $explodeName = explode(" ", $line1);
    $explodeCase = explode(" ", $line2);
    $return['name'] = $explodeName[count($explodeName) - 1];
    $return['time'] = substr($explodeName[count($explodeName) - 1], 4, -5);
    $return['var'] = "geneticData_" . substr($explodeName[count($explodeName) - 1], 4, -5);
    if ($sv == 0) {
        $return['case'] = "data_" . substr($explodeCase[count($explodeCase) - 1], 0, -4) . "js";
        $return['var_case'] = "geneticData_" . substr($explodeCase[count($explodeCase) - 1], 0, -4);
    } else {
        $return['case'] = "data_" . substr($explodeCase[count($explodeCase) - 1], 0, -5) . '_sv.' . "js";
        $return['var_case'] = "geneticData_" . substr($explodeCase[count($explodeCase) - 1], 0, -5) . '_sv.';
    }
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