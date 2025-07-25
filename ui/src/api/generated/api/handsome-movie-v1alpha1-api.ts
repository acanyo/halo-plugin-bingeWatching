/* tslint:disable */
/* eslint-disable */
/**
 * Halo
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: 2.20.14
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import type { Configuration } from '../configuration';
import type { AxiosPromise, AxiosInstance, RawAxiosRequestConfig } from 'axios';
import globalAxios from 'axios';
// Some imports not used depending on template conditions
// @ts-ignore
import { DUMMY_BASE_URL, assertParamExists, setApiKeyToObject, setBasicAuthToObject, setBearerAuthToObject, setOAuthToObject, setSearchParams, serializeDataIfNeeded, toPathString, createRequestFunction } from '../common';
// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS, type RequestArgs, BaseAPI, RequiredError, operationServerMap } from '../base';
// @ts-ignore
import type { HandsomeMovie } from '../models';
// @ts-ignore
import type { HandsomeMovieList } from '../models';
// @ts-ignore
import type { JsonPatchInner } from '../models';
/**
 * HandsomeMovieV1alpha1Api - axios parameter creator
 * @export
 */
export const HandsomeMovieV1alpha1ApiAxiosParamCreator = function (configuration?: Configuration) {
    return {
        /**
         * Create HandsomeMovie
         * @param {HandsomeMovie} [handsomeMovie] Fresh handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        createHandsomeMovie: async (handsomeMovie?: HandsomeMovie, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'POST', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(handsomeMovie, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Delete HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteHandsomeMovie: async (name: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'name' is not null or undefined
            assertParamExists('deleteHandsomeMovie', 'name', name)
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies/{name}`
                .replace(`{${"name"}}`, encodeURIComponent(String(name)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'DELETE', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Get HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getHandsomeMovie: async (name: string, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'name' is not null or undefined
            assertParamExists('getHandsomeMovie', 'name', name)
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies/{name}`
                .replace(`{${"name"}}`, encodeURIComponent(String(name)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * List HandsomeMovie
         * @param {number} [page] Page number. Default is 0.
         * @param {number} [size] Size number. Default is 0.
         * @param {Array<string>} [labelSelector] Label selector. e.g.: hidden!&#x3D;true
         * @param {Array<string>} [fieldSelector] Field selector. e.g.: metadata.name&#x3D;&#x3D;halo
         * @param {Array<string>} [sort] Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        listHandsomeMovie: async (page?: number, size?: number, labelSelector?: Array<string>, fieldSelector?: Array<string>, sort?: Array<string>, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies`;
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'GET', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)

            if (page !== undefined) {
                localVarQueryParameter['page'] = page;
            }

            if (size !== undefined) {
                localVarQueryParameter['size'] = size;
            }

            if (labelSelector) {
                localVarQueryParameter['labelSelector'] = labelSelector;
            }

            if (fieldSelector) {
                localVarQueryParameter['fieldSelector'] = fieldSelector;
            }

            if (sort) {
                localVarQueryParameter['sort'] = sort;
            }


    
            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Patch HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {Array<JsonPatchInner>} [jsonPatchInner] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        patchHandsomeMovie: async (name: string, jsonPatchInner?: Array<JsonPatchInner>, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'name' is not null or undefined
            assertParamExists('patchHandsomeMovie', 'name', name)
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies/{name}`
                .replace(`{${"name"}}`, encodeURIComponent(String(name)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'PATCH', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            localVarHeaderParameter['Content-Type'] = 'application/json-patch+json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(jsonPatchInner, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
        /**
         * Update HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {HandsomeMovie} [handsomeMovie] Updated handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateHandsomeMovie: async (name: string, handsomeMovie?: HandsomeMovie, options: RawAxiosRequestConfig = {}): Promise<RequestArgs> => {
            // verify required parameter 'name' is not null or undefined
            assertParamExists('updateHandsomeMovie', 'name', name)
            const localVarPath = `/apis/bingewatching.lik.cc/v1alpha1/handsomemovies/{name}`
                .replace(`{${"name"}}`, encodeURIComponent(String(name)));
            // use dummy base URL string because the URL constructor only accepts absolute URLs.
            const localVarUrlObj = new URL(localVarPath, DUMMY_BASE_URL);
            let baseOptions;
            if (configuration) {
                baseOptions = configuration.baseOptions;
            }

            const localVarRequestOptions = { method: 'PUT', ...baseOptions, ...options};
            const localVarHeaderParameter = {} as any;
            const localVarQueryParameter = {} as any;

            // authentication basicAuth required
            // http basic authentication required
            setBasicAuthToObject(localVarRequestOptions, configuration)

            // authentication bearerAuth required
            // http bearer authentication required
            await setBearerAuthToObject(localVarHeaderParameter, configuration)


    
            localVarHeaderParameter['Content-Type'] = 'application/json';

            setSearchParams(localVarUrlObj, localVarQueryParameter);
            let headersFromBaseOptions = baseOptions && baseOptions.headers ? baseOptions.headers : {};
            localVarRequestOptions.headers = {...localVarHeaderParameter, ...headersFromBaseOptions, ...options.headers};
            localVarRequestOptions.data = serializeDataIfNeeded(handsomeMovie, localVarRequestOptions, configuration)

            return {
                url: toPathString(localVarUrlObj),
                options: localVarRequestOptions,
            };
        },
    }
};

/**
 * HandsomeMovieV1alpha1Api - functional programming interface
 * @export
 */
export const HandsomeMovieV1alpha1ApiFp = function(configuration?: Configuration) {
    const localVarAxiosParamCreator = HandsomeMovieV1alpha1ApiAxiosParamCreator(configuration)
    return {
        /**
         * Create HandsomeMovie
         * @param {HandsomeMovie} [handsomeMovie] Fresh handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async createHandsomeMovie(handsomeMovie?: HandsomeMovie, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<HandsomeMovie>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.createHandsomeMovie(handsomeMovie, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.createHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Delete HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async deleteHandsomeMovie(name: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<void>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.deleteHandsomeMovie(name, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.deleteHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Get HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async getHandsomeMovie(name: string, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<HandsomeMovie>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.getHandsomeMovie(name, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.getHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * List HandsomeMovie
         * @param {number} [page] Page number. Default is 0.
         * @param {number} [size] Size number. Default is 0.
         * @param {Array<string>} [labelSelector] Label selector. e.g.: hidden!&#x3D;true
         * @param {Array<string>} [fieldSelector] Field selector. e.g.: metadata.name&#x3D;&#x3D;halo
         * @param {Array<string>} [sort] Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async listHandsomeMovie(page?: number, size?: number, labelSelector?: Array<string>, fieldSelector?: Array<string>, sort?: Array<string>, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<HandsomeMovieList>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.listHandsomeMovie(page, size, labelSelector, fieldSelector, sort, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.listHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Patch HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {Array<JsonPatchInner>} [jsonPatchInner] 
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async patchHandsomeMovie(name: string, jsonPatchInner?: Array<JsonPatchInner>, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<HandsomeMovie>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.patchHandsomeMovie(name, jsonPatchInner, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.patchHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
        /**
         * Update HandsomeMovie
         * @param {string} name Name of handsomemovie
         * @param {HandsomeMovie} [handsomeMovie] Updated handsomemovie
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        async updateHandsomeMovie(name: string, handsomeMovie?: HandsomeMovie, options?: RawAxiosRequestConfig): Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<HandsomeMovie>> {
            const localVarAxiosArgs = await localVarAxiosParamCreator.updateHandsomeMovie(name, handsomeMovie, options);
            const localVarOperationServerIndex = configuration?.serverIndex ?? 0;
            const localVarOperationServerBasePath = operationServerMap['HandsomeMovieV1alpha1Api.updateHandsomeMovie']?.[localVarOperationServerIndex]?.url;
            return (axios, basePath) => createRequestFunction(localVarAxiosArgs, globalAxios, BASE_PATH, configuration)(axios, localVarOperationServerBasePath || basePath);
        },
    }
};

/**
 * HandsomeMovieV1alpha1Api - factory interface
 * @export
 */
export const HandsomeMovieV1alpha1ApiFactory = function (configuration?: Configuration, basePath?: string, axios?: AxiosInstance) {
    const localVarFp = HandsomeMovieV1alpha1ApiFp(configuration)
    return {
        /**
         * Create HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        createHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest = {}, options?: RawAxiosRequestConfig): AxiosPromise<HandsomeMovie> {
            return localVarFp.createHandsomeMovie(requestParameters.handsomeMovie, options).then((request) => request(axios, basePath));
        },
        /**
         * Delete HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        deleteHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest, options?: RawAxiosRequestConfig): AxiosPromise<void> {
            return localVarFp.deleteHandsomeMovie(requestParameters.name, options).then((request) => request(axios, basePath));
        },
        /**
         * Get HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        getHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest, options?: RawAxiosRequestConfig): AxiosPromise<HandsomeMovie> {
            return localVarFp.getHandsomeMovie(requestParameters.name, options).then((request) => request(axios, basePath));
        },
        /**
         * List HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiListHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        listHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiListHandsomeMovieRequest = {}, options?: RawAxiosRequestConfig): AxiosPromise<HandsomeMovieList> {
            return localVarFp.listHandsomeMovie(requestParameters.page, requestParameters.size, requestParameters.labelSelector, requestParameters.fieldSelector, requestParameters.sort, options).then((request) => request(axios, basePath));
        },
        /**
         * Patch HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        patchHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest, options?: RawAxiosRequestConfig): AxiosPromise<HandsomeMovie> {
            return localVarFp.patchHandsomeMovie(requestParameters.name, requestParameters.jsonPatchInner, options).then((request) => request(axios, basePath));
        },
        /**
         * Update HandsomeMovie
         * @param {HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest} requestParameters Request parameters.
         * @param {*} [options] Override http request option.
         * @throws {RequiredError}
         */
        updateHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest, options?: RawAxiosRequestConfig): AxiosPromise<HandsomeMovie> {
            return localVarFp.updateHandsomeMovie(requestParameters.name, requestParameters.handsomeMovie, options).then((request) => request(axios, basePath));
        },
    };
};

/**
 * Request parameters for createHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest {
    /**
     * Fresh handsomemovie
     * @type {HandsomeMovie}
     * @memberof HandsomeMovieV1alpha1ApiCreateHandsomeMovie
     */
    readonly handsomeMovie?: HandsomeMovie
}

/**
 * Request parameters for deleteHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest {
    /**
     * Name of handsomemovie
     * @type {string}
     * @memberof HandsomeMovieV1alpha1ApiDeleteHandsomeMovie
     */
    readonly name: string
}

/**
 * Request parameters for getHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest {
    /**
     * Name of handsomemovie
     * @type {string}
     * @memberof HandsomeMovieV1alpha1ApiGetHandsomeMovie
     */
    readonly name: string
}

/**
 * Request parameters for listHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiListHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiListHandsomeMovieRequest {
    /**
     * Page number. Default is 0.
     * @type {number}
     * @memberof HandsomeMovieV1alpha1ApiListHandsomeMovie
     */
    readonly page?: number

    /**
     * Size number. Default is 0.
     * @type {number}
     * @memberof HandsomeMovieV1alpha1ApiListHandsomeMovie
     */
    readonly size?: number

    /**
     * Label selector. e.g.: hidden!&#x3D;true
     * @type {Array<string>}
     * @memberof HandsomeMovieV1alpha1ApiListHandsomeMovie
     */
    readonly labelSelector?: Array<string>

    /**
     * Field selector. e.g.: metadata.name&#x3D;&#x3D;halo
     * @type {Array<string>}
     * @memberof HandsomeMovieV1alpha1ApiListHandsomeMovie
     */
    readonly fieldSelector?: Array<string>

    /**
     * Sorting criteria in the format: property,(asc|desc). Default sort order is ascending. Multiple sort criteria are supported.
     * @type {Array<string>}
     * @memberof HandsomeMovieV1alpha1ApiListHandsomeMovie
     */
    readonly sort?: Array<string>
}

/**
 * Request parameters for patchHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest {
    /**
     * Name of handsomemovie
     * @type {string}
     * @memberof HandsomeMovieV1alpha1ApiPatchHandsomeMovie
     */
    readonly name: string

    /**
     * 
     * @type {Array<JsonPatchInner>}
     * @memberof HandsomeMovieV1alpha1ApiPatchHandsomeMovie
     */
    readonly jsonPatchInner?: Array<JsonPatchInner>
}

/**
 * Request parameters for updateHandsomeMovie operation in HandsomeMovieV1alpha1Api.
 * @export
 * @interface HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest
 */
export interface HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest {
    /**
     * Name of handsomemovie
     * @type {string}
     * @memberof HandsomeMovieV1alpha1ApiUpdateHandsomeMovie
     */
    readonly name: string

    /**
     * Updated handsomemovie
     * @type {HandsomeMovie}
     * @memberof HandsomeMovieV1alpha1ApiUpdateHandsomeMovie
     */
    readonly handsomeMovie?: HandsomeMovie
}

/**
 * HandsomeMovieV1alpha1Api - object-oriented interface
 * @export
 * @class HandsomeMovieV1alpha1Api
 * @extends {BaseAPI}
 */
export class HandsomeMovieV1alpha1Api extends BaseAPI {
    /**
     * Create HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public createHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiCreateHandsomeMovieRequest = {}, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).createHandsomeMovie(requestParameters.handsomeMovie, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Delete HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public deleteHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiDeleteHandsomeMovieRequest, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).deleteHandsomeMovie(requestParameters.name, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Get HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public getHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiGetHandsomeMovieRequest, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).getHandsomeMovie(requestParameters.name, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * List HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiListHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public listHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiListHandsomeMovieRequest = {}, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).listHandsomeMovie(requestParameters.page, requestParameters.size, requestParameters.labelSelector, requestParameters.fieldSelector, requestParameters.sort, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Patch HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public patchHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiPatchHandsomeMovieRequest, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).patchHandsomeMovie(requestParameters.name, requestParameters.jsonPatchInner, options).then((request) => request(this.axios, this.basePath));
    }

    /**
     * Update HandsomeMovie
     * @param {HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest} requestParameters Request parameters.
     * @param {*} [options] Override http request option.
     * @throws {RequiredError}
     * @memberof HandsomeMovieV1alpha1Api
     */
    public updateHandsomeMovie(requestParameters: HandsomeMovieV1alpha1ApiUpdateHandsomeMovieRequest, options?: RawAxiosRequestConfig) {
        return HandsomeMovieV1alpha1ApiFp(this.configuration).updateHandsomeMovie(requestParameters.name, requestParameters.handsomeMovie, options).then((request) => request(this.axios, this.basePath));
    }
}

