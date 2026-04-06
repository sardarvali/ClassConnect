const fs = require('fs');
const path = require('path');

function walk(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        file = path.join(dir, file);
        const stat = fs.statSync(file);
        if (stat && stat.isDirectory()) {
            results = results.concat(walk(file));
        } else if (file.endsWith('.xml')) {
            results.push(file);
        }
    });
    return results;
}

const files = walk('app/src/main/res/layout');
files.forEach(f => {
    let oldContent = fs.readFileSync(f, 'utf8');
    let newContent = oldContent
        .replace(/"topend"/g, '"top|end"')
        .replace(/"bottomend"/g, '"bottom|end"')
        .replace(/"topstart"/g, '"top|start"')
        .replace(/"bottomstart"/g, '"bottom|start"')
        .replace(/"bottomcenter_horizontal"/g, '"bottom|center_horizontal"')
        .replace(/"centervertical"/g, '"center_vertical"')
        .replace(/"centerhorizontal"/g, '"center_horizontal"');

    if (oldContent !== newContent) {
        fs.writeFileSync(f, newContent, 'utf8');
        console.log('Fixed', f);
    }
});
