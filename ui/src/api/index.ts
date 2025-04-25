import type {AxiosInstance} from "axios";
import axios from "axios";
import type {HandsomeMovie} from "./generated";
import {consoleApiClient} from "@halo-dev/api-client";

export interface Option {
  label: string;
  value: string;
}

export interface HandsomeMovieList {
  page: number;
  size: number;
  total: number;
  items: HandsomeMovie[];
}

export class HandsomeMovieApi {
  private axios: AxiosInstance;
  private baseUrl: string = "/apis/bingewatching.lik.cc/v1alpha1";

  constructor(axios: AxiosInstance) {
    this.axios = axios;
  }

  /**
   * 获取影视列表
   */
  async listHandsomeMovies(params?: {
    page?: number;
    size?: number;
    keyword?: string;
    sort?: string[];
    type?: string;
  }): Promise<HandsomeMovieList> {
    const { data } = await this.axios.get(
      `${this.baseUrl}/handsomemovies`,
      {
        params: {
          page: params?.page || 1,
          size: params?.size || 20,
          keyword: params?.keyword,
          sort: params?.sort,
          type: params?.type,
        },
      }
    );
    return data;
  }

  /**
   * 获取单个影视
   */
  async getHandsomeMovie(name: string): Promise<HandsomeMovie> {
    const { data } = await this.axios.get(
      `${this.baseUrl}/handsomemovies/${name}`
    );
    return data;
  }

  /**
   * 创建影视
   */
  async createHandsomeMovie(handsomeMovie: HandsomeMovie): Promise<HandsomeMovie> {
    const { data } = await this.axios.post(
      `${this.baseUrl}/handsomemovies`,
      handsomeMovie
    );
    return data;
  }

  /**
   * 更新影视
   */
  async updateHandsomeMovie(name: string, handsomeMovie: HandsomeMovie): Promise<HandsomeMovie> {
    const { data } = await this.axios.put(
      `${this.baseUrl}/handsomemovies/${name}`,
      handsomeMovie
    );
    return data;
  }

  /**
   * 删除影视
   */
  async deleteHandsomeMovie(name: string): Promise<void> {
    await this.axios.delete(`${this.baseUrl}/handsomemovies/${name}`);
  }

  /**
   * 批量删除影视
   */
  async deleteHandsomeMovies(names: string[]): Promise<void> {
    const promises = names.map((name) => this.deleteHandsomeMovie(name));
    await Promise.all(promises);
  }

  /**
   * 获取影视类型列表
   */
  async listHandsomeMovieStatus(): Promise<Option[]> {
    try {
      const { data } = await consoleApiClient.plugin.plugin.fetchPluginConfig({
        name: 'handsomemovies'
      });

      const { advanced } = data?.data ?? {};
      const { listMovieStatus = [] } = advanced ? JSON.parse(advanced) : {};

      return listMovieStatus.map((type: string) => ({
        label: type,
        value: type
      }));
    } catch (error) {
      console.error("Failed to fetch handsomemovies config:", error);
      return [];
    }
  }
}

// 创建实例并导出
export const handsomeMovieApi = new HandsomeMovieApi(axios.create()); 
