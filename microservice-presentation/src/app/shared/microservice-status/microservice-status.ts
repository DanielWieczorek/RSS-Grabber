export class MicroserviceStatus {

    bindHostname: string;
    bindPort: number;
    name: string;
    features: MicroserviceFeature[];
    status: string;
}


export class MicroserviceFeature {
    actions: MicroserviceAction[];
    description: string;
    type: string;
}

export class MicroserviceAction {
    method: string;
    name: string;
    path: string;
}
