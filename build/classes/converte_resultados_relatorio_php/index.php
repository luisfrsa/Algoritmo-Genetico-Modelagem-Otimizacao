<?php

echo "<pre>";
$dirLog = "log/";
$scan = scandir($dirLog);
foreach ($scan as $dir) {
    if ($dir != '..' and $dir != '.') {
        $content = file_get_contents($dirLog . $dir);
        $explodeLinha = explode("\n", $content);
//        print_r($explodeLinha);
        $meta = getMeta($explodeLinha[0], $explodeLinha[1]);
        print_r($meta);
        for ($i = 12; $i < (count($explodeLinha) - 4); $i++) {
            $data[] = getData($explodeLinha[$i]);
        }
        $data[] = getBest($explodeLinha[count($explodeLinha) - 2], $data[count($data) - 1]['indice']);
        print_r($data);
        break;
    }
}

function getMeta($line1, $line2) {
    $explodeName = explode(" ", $line1);
    $explodeCase = explode(" ", $line2);
    $return['name'] = $explodeName[count($explodeName) - 1];
    $return['var'] = "geneticData_" . substr($explodeName[count($explodeName) - 1], 4, -5);
    $return['case'] = "data_" . substr($explodeCase[count($explodeCase) - 1], 0, -4) . "js";
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