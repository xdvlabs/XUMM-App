const { Then } = require('cucumber');
const { element, by, waitFor } = require('detox');

Then('I agree all disclaimers', async () => {
    for (let i = 0; i < 7; i++) {
        await waitFor(element(by.id('agree-check-box')))
            .toBeVisible()
            .withTimeout(20000);
        await element(by.id('agree-check-box')).tap();
    }
});

Then('I skip biomterics if present', async () => {
    try {
        await waitFor(element(by.id('biometric-setup-view')))
            .toBeVisible()
            .withTimeout(2000);
        await element(by.id('skip-button')).tap();
    } catch {
        // ignore
    }
});
