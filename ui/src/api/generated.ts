export interface HandsomeMovie {
  apiVersion: string;
  kind: string;
  metadata: {
    name: string;
    generateName?: string;
    creationTimestamp?: string;
    deletionTimestamp?: string;
    labels?: Record<string, string>;
    annotations?: Record<string, string>;
  };
  spec: {
    vodName: string;
    vodEn?: string;
    vodPic: string;
    vodActor?: string;
    vodLang?: string;
    vodYear?: string;
    vodScore?: string;
    vodContent?: string;
    typeName?: string;
    seen?: string;
    updateCycle?: string;
    status?: string;
    newSeen?: string;
    classicLines?: string[];
  };
}

export interface ListResult<T> {
  items: T[];
  total: number;
  page: number;
  size: number;
}

export interface HandsomeMovieList {
  items: HandsomeMovie[];
  total: number;
  page: number;
  size: number;
}

// 添加 handsomeMovie API 客户端类型定义
export interface HandsomeMovieV1alpha1UcApi {
  listHandsomeMovies(params: {
    page?: number;
    size?: number;
    sort?: string;
    type?: string;
    keyword?: string;
  }): Promise<{ data: ListResult<HandsomeMovie> }>;
  
  createHandsomeMovie(params: { handsomeMovie: HandsomeMovie }): Promise<{ data: HandsomeMovie }>;
  
  updateHandsomeMovie(params: { name: string; handsomeMovie: HandsomeMovie }): Promise<{ data: HandsomeMovie }>;
  
  deleteHandsomeMovie(params: { name: string }): Promise<void>;
}

/**
 * 选项
 **/
export interface Option {
  label: string;
  value: string;
}

// 扩展 ucApiClient 类型
declare module "@halo-dev/api-client" {
  interface UcApiClient {
    handsomeMovie: HandsomeMovieV1alpha1UcApi;
  }
} 
