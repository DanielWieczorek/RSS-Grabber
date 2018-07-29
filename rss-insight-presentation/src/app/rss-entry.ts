export class RssEntry {
    
    uri: string;
    description: string;
    feedurl: string;
    heading: string;
    publicationdate: Date;
    createdAt: Date;
}

export class RssEntrySentiment {
    
    entry: RssEntry;
    negativeProbability: number;
    positiveProbability: number;
}

export class RssEntrySentimentSummary {
    
    negativeProbability: number;
    positiveProbability: number;
}
export class SentimentEvaluationResult {
    
    sentiments: RssEntrySentiment[];
    summary: RssEntrySentimentSummary;
}