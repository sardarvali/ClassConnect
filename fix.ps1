$files = Get-ChildItem -Path app\src\main\res\layout -Filter *.xml -Recurse
foreach ($f in $files) {
    if (Test-Path $f.FullName) {
        $content = [System.IO.File]::ReadAllText($f.FullName)
        $newContent = $content.Replace("topend", "top|end").Replace("bottomend", "bottom|end").Replace("topstart", "top|start").Replace("bottomstart", "bottom|start").Replace("bottomcenter_horizontal", "bottom|center_horizontal")
        if ($content -ne $newContent) {
            [System.IO.File]::WriteAllText($f.FullName, $newContent)
            Write-Host "Fixed $($f.Name)"
        }
    }
}

