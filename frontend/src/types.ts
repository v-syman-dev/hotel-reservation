export interface Address {
    id?: number;
    country: string;
    city: string;
    street: string;
}

export interface Convenience {
    id?: number;
    name: string;
}

export interface Room {
    id?: number;
    number: number;
    type: string;
    pricePerNight: number;
}

export interface Hotel {
    id?: number;
    name: string;
    rating: number;
    address: Address; 
    rooms: Room[];    
    conveniences: Convenience[]; 
}
