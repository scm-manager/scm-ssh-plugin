/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { requiredLink } from "@scm-manager/ui-api";
import { apiClient } from "@scm-manager/ui-components";
import { Me, User } from "@scm-manager/ui-types";
import { AuthorizedKey, AuthorizedKeyCreation, AuthorizedKeysCollection } from "./types";

export type ApiResult<T> = {
  isLoading: boolean;
  error: Error | null;
  data?: T;
};

const CONTENT_TYPE_API_KEY = "application/vnd.scmm-authorizedkey+json;v=2";

export const useAuthorizedKeys = (user: User | Me): ApiResult<AuthorizedKeysCollection> =>
  useQuery({
    queryKey: ["user", user.name, "authorizedKeys"],
    queryFn: () => apiClient.get(requiredLink(user, "authorized_keys")).then((r) => r.json()),
  });

const createAuthorizedKey =
  (link: string) =>
  async (key: AuthorizedKeyCreation): Promise<AuthorizedKey> => {
    const creationResponse = await apiClient.post(link, key, CONTENT_TYPE_API_KEY);
    const location = creationResponse.headers.get("Location");
    if (!location) {
      throw new Error("Server does not return required Location header");
    }
    const authorizedKeyResponse = await apiClient.get(location);
    return authorizedKeyResponse.json();
  };

export const useCreateAuthorizedKey = (user: User | Me, authorizedKeys: AuthorizedKeysCollection) => {
  const queryClient = useQueryClient();
  const {
    mutate,
    data,
    isPending: isLoading,
    error,
    reset,
  } = useMutation<AuthorizedKey, Error, AuthorizedKeyCreation>({
    mutationFn: createAuthorizedKey(requiredLink(authorizedKeys, "create")),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["user", user.name, "authorizedKeys"] }),
  });
  return {
    create: (key: AuthorizedKeyCreation) => mutate(key),
    isLoading,
    error,
    authorizedKey: data,
    reset,
  };
};

export const useDeleteAuthorizedKey = (user: User | Me) => {
  const queryClient = useQueryClient();
  const {
    mutate,
    data,
    isPending: isLoading,
    error,
  } = useMutation<unknown, Error, AuthorizedKey>({
    mutationFn: (authorizedKey) => {
      const deleteUrl = requiredLink(authorizedKey, "delete");
      return apiClient.delete(deleteUrl);
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["user", user.name, "authorizedKeys"] }),
  });
  return {
    remove: (authorizedKey: AuthorizedKey) => mutate(authorizedKey),
    isLoading,
    error,
    isDeleted: !!data,
  };
};
