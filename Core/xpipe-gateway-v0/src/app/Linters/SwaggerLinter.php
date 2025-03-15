<?php
declare(strict_types=1);

namespace App\Linters;

use App\Models\Swagger;

use Symfony\Component\Yaml\Exception\ParseException;
use Symfony\Component\Yaml\Yaml;

use Symfony\Component\Serializer\Exception\PartialDenormalizationException;
use Symfony\Component\Serializer\Normalizer\DenormalizerInterface;
use Symfony\Component\Serializer\Encoder\JsonEncoder;
use Symfony\Component\Serializer\Encoder\YamlEncoder;
use Symfony\Component\Serializer\Normalizer\ObjectNormalizer;
use Symfony\Component\Serializer\Serializer;

class SwaggerLinter extends BaseLinter
{

    /**
     * Linter handle
     *
     * @param string $content
     * @return array
     */
    public function handle(string $content): array
    {
        $checkYamlFormat = $this->isValidYamlFormat($content);

        if (! $checkYamlFormat['success']) {
            return $this->sendError($checkYamlFormat['line'], $checkYamlFormat['snippet'], $checkYamlFormat['message']);
        }

        $parser = $this->parser($content);
        if (! $parser['success']) {
            return $this->sendError($parser['line'], $parser['snippet'], $parser['message']);
        }

        return $this->sendResponse();



        // OOP type processing

        //$encoders = [new YamlEncoder, new JsonEncoder()];
        //$normalizers = [new ObjectNormalizer()];

        //$serializer = new Serializer($normalizers, $encoders);

        //try {
            //$swagger = $serializer->deserialize($content, Swagger::class, 'yaml', [DenormalizerInterface::COLLECT_DENORMALIZATION_ERRORS => true,]);
            //dd($swagger);
        //} catch (PartialDenormalizationException $e) {
        //    dd($e->getErrors());

//            $violations = new ConstraintViolationList();
//            /** @var NotNormalizableValueException $exception */
//            foreach ($e->getErrors() as $exception) {
//                $message = sprintf('The type must be one of "%s" ("%s" given).', implode(', ', $exception->getExpectedTypes()), $exception->getCurrentType());
//                $parameters = [];
//                if ($exception->canUseMessageForUser()) {
//                    $parameters['hint'] = $exception->getMessage();
//                }
//                $violations->add(new ConstraintViolation($message, '', $parameters, null, $exception->getPath(), null));
//            }
//
//            return $this->json($violations, 400);

        //}


    }


    /**
     * Swagger parser
     *
     * @param string $content
     * @return array
     */
    private function parser(string $content) : array
    {
        $data = Yaml::parse($content);

        $eachLine = explode(PHP_EOL, $content);

        if (! $this->firstLineExist($eachLine)) {
            return $this->sendError(1, 'null', 'The first line doesn\'t exist ');
        }

        $firstLine = $this->matchYamlSeparator($eachLine[0]);
        if (! $firstLine) {
            return $this->sendError(1, $eachLine[0], 'The first line of file must be a yaml separator --- ');
        }

        if (! $this->secondLineExist($eachLine)) {
            return $this->sendError(2, 'null', 'The second line doesn\'t exist ');
        }

        $secondLine = $this->matchVersionKey($eachLine[1]);
        if (! $secondLine) {
            return $this->sendError(2, $eachLine[1], 'The second line of file must be key version: with value x.y.z ');
        }

        if (! $this->thirdLineExist($eachLine)) {
            return $this->sendError(3, 'null', 'The third line doesn\'t exist ');
        }

        $thirdLine = $this->matchRoutesKey($eachLine[2]);
        if (! $thirdLine) {
            return $this->sendError(3, $eachLine[2], 'The third line of file must be key routes: ');
        }

        $mainKeysAreAlone = $this->mainKeysAreAlone($data);
        if (! $mainKeysAreAlone) {
            return $this->sendError(-1, "", 'Found one or more keys that are not allowed, main keys must be only version: and routes: ');
        }

        if ($data['routes'] === NULL) {
            return $this->sendError(3, "null", 'the key routes: must not be null ');
        }


        foreach ($data['routes'] as $key => $value) {

            if ($data['routes'][$key]['route'] === NULL) {
                return $this->sendError(-1, "", 'You have defined a route without required fields ');
            }

            $checkRouteKeysMandatory = $this->checkRouteKeysMandatory($value['route']);
            if (! $checkRouteKeysMandatory['success']) {
                return $this->sendError(-1, "", 'You have defined a route without required fields, missing ' . $checkRouteKeysMandatory['data'] . ' ');
            }


            // Disabled temp
            //$routeKeysAreAlone = $this->routeKeysAreAlone($value['route']);
            //if ($routeKeysAreAlone) {
            //    return $this->sendError(-1, "", 'Found one or more keys that are not allowed, route keys must be only name: isPublic: version: context: method: uri: comment: ');
            //}

        }


        return $this->sendResponse();
    }



    /**
     * Check if the first line exist
     *
     * @return bool
     */
    private function firstLineExist($eachLine) : bool
    {
        if (array_key_exists(0, $eachLine)) {
            return true;
        }

        return false;
    }

    /**
     * Check if the second line exist
     *
     * @return bool
     */
    private function secondLineExist($eachLine) : bool
    {
        if (array_key_exists(1, $eachLine)) {
            return true;
        }

        return false;
    }

    /**
     * Check if the third line exist
     *
     * @return bool
     */
    private function thirdLineExist($eachLine) : bool
    {
        if (array_key_exists(2, $eachLine)) {
            return true;
        }

        return false;
    }


    /**
     * Check if mandatory route keys aren't passed
     *
     * @param array $route
     * @return array
     */
    private function checkRouteKeysMandatory(array $route) : array
    {
        $mandatoryKeys = ['name', 'isPublic', 'version', 'context', 'method', 'uri'];

        $matchedKeys = array_keys($route);

        $diff = array_diff($mandatoryKeys, $matchedKeys);

        if (count($diff) !== 0) {
            //TODO: valutare se implementare il search delle righe
            return ['success' => false, 'data' => implode(",", $diff)];
        }
        return ['success' => true, 'message' => 'ok'];
    }

    /**
     * Check if route keys are alone
     *
     * @param array $route
     * @return bool
     */
    private function routeKeysAreAlone(array $route) : bool
    {
        $allowedKeys = ['name', 'isPublic', 'version', 'context', 'method', 'uri', 'comment'];

        $matchedKeys = array_keys($route);

        $diff = array_diff($matchedKeys, $allowedKeys);

        if (count($diff) !== 0) {
            //TODO: valutare se implementare il search delle righe
            return false;
        }
        return true;
    }



    /**
     * Check if main keys are alone
     *
     * @param array $data
     * @return bool
     */
    private function mainKeysAreAlone(array $data) : bool
    {
        $allowedKeys = ['version', 'routes'];

        $matchedKeys = array_keys($data);

        $diff = array_diff($matchedKeys, $allowedKeys);

        if (count($diff) !== 0) {

            //TODO: valutare se implementare il search delle righe

            return false;
        }

        return true;
    }


    /**
     * Match yaml separator
     *
     * @param string $line
     * @return bool
     */
    private function matchYamlSeparator($line) : bool
    {
        $yamlSeparator = "---";

        if ($line == $yamlSeparator) {
            return true;
        }

        return false;
    }

    /**
     * Match version key with x.y.z value with and without double quote
     *
     * @param $line
     * @return bool
     */
    private function matchVersionKey($line) : bool
    {
        if (preg_match('/(version: )([0-9]+.[0-9]+.[0-9]+|\"[0-9]+.[0-9]+.[0-9]+\")/', $line, $matches, PREG_OFFSET_CAPTURE)) {
            return true;
        }

        return false;
    }

    /**
     * Match routes key
     *
     * @param $line
     * @return bool
     */
    private function matchRoutesKey($line) : bool
    {
        $routesKey = "routes:";

        if ($line == $routesKey) {
            return true;
        }

        return false;
    }


    /**
     * Check if is a valid yaml format
     *
     * @param string $content
     * @return array
     */
    private function isValidYamlFormat(string $content) : array
    {
        try {
            Yaml::parse($content);
            return $this->sendResponse();
        } catch (ParseException $e) {
            return $this->sendError($e->getParsedLine(), $e->getSnippet(), $e->getMessage());
        }
    }




}