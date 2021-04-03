import { Trade } from './trade';
import { Account } from './account';

export class TradingSimulationResult {
     initialBalance: Account;
     finalBalance: Account;
     trades: Trade[];
}
