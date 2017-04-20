#!/bin/bash -e

stop() {
  echo $* >&2
  exit 0
}

DOCUMENTATION_BRANCHES=(
  "develop",
  "master",
  "versions/1.10.x"
)

deploy_on_ghpages() {
  local e
  for e in DOCUMENTATION_BRANCHES; do [[ "$e" == "$1" ]] return 0; done
  return 1
}

[ ! deploy_on_ghpages "${TRAVIS_BRANCH}" ] \
  || stop "do not deploy docs of ${TRAVIS_BRANCH}"

[ "${TRAVIS_SECURE_ENV_VARS}" == "true" ] \
  || stop "no secure enviroment variables were provided"

[ "${TRAVIS_JOB_NUMBER}" == "${TRAVIS_BUILD_NUMBER}.1" ] \
  || stop "only the first build job will be deployed"

[ "${TRAVIS_PULL_REQUEST}" == "false" ] \
  || stop "no deployment for pull requests"

github_name="Travis CI"
github_mail="travis@travis-ci.org"
branch="gh-pages"
docs_dir="docs"
docs_deploy_dir="./${docs_dir}/_site"
build_dir=$(mktemp -d)

git config --global user.name "${github_name}"
git config --global user.email "${github_mail}"
git clone --quiet --depth 1 --branch "${branch}" https://github.com/${TRAVIS_REPO_SLUG}.git "${build_dir}"

bundle exec jekyll build -s "${docs_dir}" 
bundle exec htmlproofer "${docs_deploy_dir}"

output_folder=$(echo $TRAVIS_BRANCH | \sed 's/\//_/')
rm -rf "${build_dir:?}/${output_folder}"
cp -rv "${docs_deploy_dir}" "${build_dir}/${output_folder}"

pushd "${build_dir}"
git add --ignore-removal .
git add --update :/
git commit -m "Updating ${TRAVIS_BRANCH} on ${branch} to ${TRAVIS_COMMIT}"
git push --force --quiet "https://${GITHUB_TOKEN}@github.com/${TRAVIS_REPO_SLUG}.git" "${branch}" >/dev/null 2>&1
popd