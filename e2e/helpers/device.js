const detox = require('detox');
const { spawn, spawnSync, kill } = require('child_process');
const { createWriteStream, existsSync, unlinkSync, mkdirSync } = require('fs');
const path = require('path');

const ARTIFACTS_DIR = path.resolve(__dirname, '../artifacts');

const startDeviceLogStream = () => {
    const platform = detox.device.getPlatform();
    const logFile = `${ARTIFACTS_DIR}/${platform}_device.log`;

    if (!existsSync(ARTIFACTS_DIR)) {
        mkdirSync(ARTIFACTS_DIR);
    }

    if (existsSync(logFile)) {
        unlinkSync(logFile);
    }

    const logStream = createWriteStream(logFile);

    let args;
    let proc;

    if (platform === 'ios') {
        args = ['simctl', 'spawn', 'booted', 'log', 'stream', '--predicate', 'process == "XUMM"'];
        proc = spawn('xcrun', args, { stdio: 'pipe' });
    } else {
        const procSync = spawnSync('adb', ['shell', 'pidof', '-s', 'com.xrpllabs.xumm']);
        const pid = procSync.stdout.toString().replace('\n', '');
        args = ['logcat', '-b', 'all', '--pid', pid];
        proc = spawn('adb', args, { stdio: 'pipe' });
    }

    proc.stdout.pipe(logStream);
    proc.stderr.pipe(logStream);

    proc.on('error', () => {
        kill(proc.pid);
    });
};

module.exports = { startDeviceLogStream };
