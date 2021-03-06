/**
 * Account Model
 */

import Realm from 'realm';

import CurrencySchema from '@store/schemas/v1/currency';
import TrustLineSchema from '@store/schemas/v1/trustLine';

import { EncryptionLevels, AccessLevels } from '@store/types';

class Account extends Realm.Object {
    public static schema: Realm.ObjectSchema = {
        name: 'Account',
        primaryKey: 'address',
        properties: {
            address: { type: 'string', indexed: true },
            label: { type: 'string', default: 'Personal account' },
            balance: { type: 'double', default: 0 },
            ownerCount: { type: 'int', default: 0 },
            sequence: { type: 'int', default: 0 },
            publicKey: 'string?',
            regularKey: 'string?',
            accessLevel: 'string',
            encryptionLevel: 'string',
            flags: { type: 'int', default: 0 },
            default: { type: 'bool', default: false },
            order: { type: 'int', default: 0 },
            lines: { type: 'list', objectType: 'TrustLine' },
            registerAt: { type: 'date', default: new Date() },
            updatedAt: { type: 'date', default: new Date() },
        },
    };

    public address?: string;
    public label?: string;
    public balance?: number;
    public ownerCount?: number;
    public sequence?: number;
    public publicKey?: string;
    public regularKey?: string;
    public accessLevel?: AccessLevels;
    public encryptionLevel?: EncryptionLevels;
    public flags?: number;
    public default?: boolean;
    public lines?: any;
    public registerAt?: Date;
    public updatedAt?: Date;

    [index: string]: any;

    constructor(obj: Partial<Account>) {
        super();
        Object.assign(this, obj);
    }

    /**
     * check if account have specific trustline
     */
    hasCurrency = (currency: CurrencySchema): boolean => {
        let found = false;

        this.lines.forEach((t: TrustLineSchema) => {
            if (t.currency.issuer === currency.issuer && t.currency.currency === currency.currency) {
                found = true;
            }
        });

        return found;
    };

    public static migration(oldRealm: any, newRealm: any) {
        /*  eslint-disable-next-line */
        console.log('migrating Account model to v2');

        const newObjects = newRealm.objects('Account') as Account[];

        for (let i = 0; i < newObjects.length; i++) {
            // set empty destination tag
            newObjects[i].order = i;
        }
    }
}

export default Account;
