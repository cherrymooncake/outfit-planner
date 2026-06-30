export interface LoginDto {
    email: string;
    password: string;
}

export interface RegisterDto {
    email: string;
    password: string;
}

export interface TokenResponseDto {
    accessToken: string;
    refreshToken: string;
}

export interface UserProfile {
    id: string;
    email: string;
    role: string;
}

export interface CategoryDto {
    id: string;
    name: string;
    isOutfitCategory: boolean;
    isItemCategory: boolean;
}

export interface TagDto {
    id: string;
    name: string;
    isOutfitTag: boolean;
    isItemTag: boolean;
}

export interface ItemFilterDto {
    searchTerm?: string;
    categoryId?: string;
    tagId?: string;
}

export interface UpdateItemDto {
    name: string;
    description?: string;
    categoryIds: string[];
    tagIds: string[];
}

export interface ItemResponseDto {
    id: string;
    name: string;
    description?: string;
    originalImageUrl: string;
    processedImageUrl: string;
    categories: CategoryDto[];
    tags: TagDto[];
}

export interface ManualMaskDto {
    contourJson: string;
}


export interface OutfitFilterDto {
    searchTerm?: string;
    categoryId?: string;
}

export interface OutfitItemDto {
    itemId: string;
    x: number;
    y: number;
    scale: number;
    rotation: number;
    zIndex: number;
}

export interface CreateOutfitDto {
    name: string;
    description?: string;
    templateId?: string | null;
    items: OutfitItemDto[];
    categoryIds?: string[];
    canvasWidth: number;
    canvasHeight: number;
}

export interface UpdateOutfitDto {
    name: string;
    description?: string;
    templateId?: string | null;
    items: OutfitItemDto[];
    categoryIds?: string[];
    canvasWidth: number;
    canvasHeight: number;
}

export interface OutfitItemResponseDto {
    id: string;
    itemId: string;
    itemImageUrl: string;
    x: number;
    y: number;
    scale: number;
    rotation: number;
    zIndex: number;
}

export interface OutfitResponseDto {
    id: string;
    name: string;
    description?: string;
    templateName?: string;
    items: OutfitItemResponseDto[];
    categories: CategoryDto[];
    canvasWidth: number;
    canvasHeight: number;
}

export interface CreateCategoryDto {
    name: string;
    isOutfitCategory: boolean;
    isItemCategory: boolean;
}

export interface UpdateCategoryDto {
    name: string;
}

export interface CreateTagDto {
    name: string;
    isOutfitTag: boolean;
    isItemTag: boolean;
}

export interface UpdateTagDto {
    name: string;
}

export interface TemplateItemDto {
    categoryIdHint?: string;
    x: number;
    y: number;
    scale: number;
    rotation: number;
    zIndex: number;
}

export interface CreateTemplateDto {
    name: string;
    description?: string;
    items: TemplateItemDto[];
}

export interface TemplateItemResponseDto {
    id: string;
    categoryIdHint?: string;
    categoryName?: string;
    x: number;
    y: number;
    scale: number;
    rotation: number;
    zIndex: number;
}

export interface TemplateResponseDto {
    id: string;
    name: string;
    description?: string;
    createdAt: string;
    updatedAt: string;
    items: TemplateItemResponseDto[];
}

export interface UpdateTemplateDto {
    name: string;
    description?: string;
    items: TemplateItemDto[];
}

export interface HealthStatusDto {
    dbStatus: string;
    bgRemovalStatus: string;
    aiStylistStatus: string;
}

export interface GlobalStatsDto {
    totalUsers: number;
    totalItems: number;
    totalOutfits: number;
    imagesFolderSizeMb: number;
}

export interface UserStatDto {
    id: string;
    email: string;
    role: string;
    registeredAt: string;
    lastActiveAt: string;
    itemsCount: number;
    outfitsCount: number;
}

export interface DailyOutfitDto {
    date: string;
    outfit?: OutfitResponseDto | null;
}

export interface SetDailyOutfitDto {
    date: string;
    outfitId: string;
}

export interface WeatherResponseDto {
    city: string;
    temperature: number;
    condition: string;
    weatherCode: number;
    recommendation: string;
}

export interface AiRecommendationRequestDto {
    prompt: string;
    weatherContext?: string;
}

export interface AiRecommendationResponseDto {
    recommendedOutfitId: string;
    explanation: string;
    outfit: OutfitResponseDto;
}
