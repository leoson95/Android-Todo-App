const fs = require('fs');
const https = require('https');
const path = require('path');

const dir = path.join(__dirname, 'app/src/main/res/font');
if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
}

const urls = [
    { name: 'vazirmatn_regular.ttf', url: 'https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Regular.ttf' },
    { name: 'vazirmatn_bold.ttf', url: 'https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Bold.ttf' },
    { name: 'vazirmatn_medium.ttf', url: 'https://github.com/rastikerdar/vazirmatn/raw/master/fonts/ttf/Vazirmatn-Medium.ttf' }
];

function download(url, dest) {
    return new Promise((resolve, reject) => {
        https.get(url, (response) => {
            if (response.statusCode === 301 || response.statusCode === 302) {
                return download(response.headers.location, dest).then(resolve, reject);
            }
            const file = fs.createWriteStream(dest);
            response.pipe(file);
            file.on('finish', () => {
                file.close(resolve);
            });
        }).on('error', (err) => {
            fs.unlink(dest, () => {});
            reject(err);
        });
    });
}

async function main() {
    for (const item of urls) {
        const dest = path.join(dir, item.name);
        console.log(`Downloading ${item.name}...`);
        await download(item.url, dest);
        console.log(`Downloaded ${item.name}`);
    }
}

main().catch(console.error);
