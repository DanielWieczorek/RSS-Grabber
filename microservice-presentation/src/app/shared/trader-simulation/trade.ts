import { Account } from './account';
export class Trade {
     date: Date;
     action: string;
     before: Account;
     after: Account;
     currentRate: number;
}
